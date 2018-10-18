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

def normalize(input):
    name = input.strip()
    name_parts = name.split(' ')
    for idx in range(len(name_parts)): 
        name_parts[idx] = name_parts[idx].lower().capitalize()
    return ' '.join(name_parts)

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

def generateQRCode(content):
    img = qrcode.make(content)
    return img

def saveQRCode(content, filename):
    file_path = f'./codes/{filename}qrcode'
    qr_code = generateQRCode(content)
    qr_code.save(file_path, 'png')
    return file_path

# #Change to encode for customer ID
# def encodeEmails(list):
#     for attendee in list:
#         qrcode = generateQRCode(attendee["Email"])
#         qrcode.save(f'./codes/{attendee["First Name"]}QRCode', 'png')
#         attendee["QR Code"] = f'{attendee["First Name"]}{attendee["Last Name"]}QRCode.png'

#Create Customers with given merchant ID and token
#implement retry after/exponential backoff. Will definitely hit rate limit in practice 
token = getToken()
env = 'https://apisandbox.dev.clover.com'
mid = 'J96JE3HEHBDG1'

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
        attendee["QR Code"] = saveQRCode(attendee["Customer ID"], uuid.uuid4().hex)


attendees = parseContactList('MOCK_DATA.csv')
createCustomers(attendees)


for attendee in attendees:
    print(f'\t{attendee["First Name"]} {attendee["Last Name"]}\'s Customer ID is {attendee["Customer ID"]}.')
    print(f'\t\t{attendee["First Name"]} {attendee["Last Name"]}\'s QR Code is located at {attendee["QR Code"]}')

# with open('Test Email List.csv', mode='w') as target_csv:
#     field_names = ['Email', 'First Name', 'Last Name', 'QR Code']
#     writer = csv.DictWriter(target_csv, field_names=fieldnames)

#     writer.writeheader()
#     writer.writerow({'emp_name': 'John Smith', 'dept': 'Accounting', 'birth_month': 'November'})
#     writer.writerow({'emp_name': 'Erica Meyers', 'dept': 'IT', 'birth_month': 'March'})

