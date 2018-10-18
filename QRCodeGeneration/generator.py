# Take in infoirmation about csv email list, merchantID, etc.
# Get OAuth Token
# Read First names, Last names and emails into a dictionary
# Go through the list to create Customers based on CSV and generate QR codes, appending IDs and codes to dictionary entries
# Write dictionary to CSV
import csv
import qrcode
import requests
import random
import logging
import json
import uuid

# sandbox_host = 'sandbox.dev.clover.com'
# prod_host = 'clover.com'
# host = ''
token = ''

# env = input('What environment is this in? (sandbox/prod)')
# choice = input('Is there a .csv you\'d like to import?')
# csv_file = input('Where is it located?')
# input('Please enter merchant email')
# pwd = input('Please enter merchant password') 

# def getURL(endpoint):
#     if(host == 'sandbox'):
#         return sandbox_host + endpoint
#     :
#     return prod_host + endpoint
    
def getToken():
    return input('Token pls: ')

def getFile():
    return input('File pls (must be in the same folder): ')

token = getToken()
env = 'https://apisandbox.dev.clover.com'
mid = 'J96JE3HEHBDG1'
file = getFile()

#Takes a string and strips it of extraneous spaces and capitalizes each word
def normalize(input):
    name = input.strip()
    name_parts = name.split(' ')
    for idx in range(len(name_parts)): 
        name_parts[idx] = name_parts[idx].lower().capitalize()
    return ' '.join(name_parts)

#Returns a list of dictionary objects from a CSV
def parseContactList(list):
    parsed_list = []

    with open(list, mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        line_count = 0
        for row in csv_reader:
            if line_count == 0:
                print(f'Column names are {", ".join(row)}')
                line_count += 1
            row["First Name"] = normalize(row["First Name"])
            row["Last Name"] = normalize(row["Last Name"])
            row["Email"] = row["Email"].strip()
            parsed_list.append(row)
        print(f'Processed {line_count} lines.')    

    return parsed_list

#Generate a QR code with the passed in content
def generateQRCode(content):
    img = qrcode.make(content)
    return img

#Make a QR code and save it with the passed in file name
def saveQRCode(content, filename):
    file_path = f'./codes/{filename}'
    qr_code = generateQRCode(content)
    qr_code.save(file_path, 'png')
    return file_path

#create a single customer. returns Customer ID        
def createCustomer(fn='', ln='', email=''):
    request_url = env + "/v3/merchants/" + mid + '/customers'
    max_attempts = 10
    attempts = 0
    response = ''
    payload = {"firstName": fn, "lastName": ln, "marketingAllowed": False}
    # Retry attempts should max out after a reasonable number of attemps.

    while attempts < max_attempts:  
        # Make a request to Clover REST API
        response = requests.post(request_url, headers={'Authorization': 'Bearer ' + token}, data=json.dumps(payload))
    
        # Checks if the response is rate limited
        if(response.status_code == 429):
            retry_duration = int(response.headers.get('retry-after'))
            logging.error(response.text + "\n" + str(response.headers))
            time.sleep(retry_duration + random.random()) #Add jitter to sleep duration.
        # Checks if there is another error
        elif(response.status_code>=400):
            logging.error(response.text + "\n" + str(response.headers))
        # If successful, break out of while loop and continue with the rest of the code
        elif response.status_code == 200:
            logging.info(str(response.status_code) + " " + request_url)
            print(response.text)
            break
        else:
            print("There was an issue completing the request. Status code received: " + response.status_code)
    
        attempts = attempts + 1
        if(attempts >= max_attempts):
            logging.error('Failed: Maxed out attempts')

    return response.json()["id"]

def createCustomers(list):
    for attendee in list:
        attendee["Customer ID"] = createCustomer(attendee["First Name"], attendee["Last Name"])
        attendee["QR Code"] = saveQRCode(attendee["Customer ID"], attendee["Last Name"] + attendee["Customer ID"])


attendees = parseContactList(file)
createCustomers(attendees)

for attendee in attendees:
    print(f'\t{attendee["First Name"]} {attendee["Last Name"]}\'s Customer ID is {attendee["Customer ID"]}.')
    print(f'\t\t{attendee["First Name"]} {attendee["Last Name"]}\'s QR Code is located at {attendee["QR Code"]}')

with open(f'Final Copy of {file}', mode='w+') as target_csv:
    field_names = ['First Name', 'Last Name', 'Email', 'Customer ID', 'QR Code']
    writer = csv.DictWriter(target_csv, fieldnames=field_names)

    writer.writeheader()
    for attendee in attendees:
        writer.writerow({'First Name': attendee['First Name'], 'Last Name': attendee['Last Name'], 'Email': attendee['Email'], 'Customer ID': attendee['Customer ID'], 'QR Code': attendee['QR Code']})

