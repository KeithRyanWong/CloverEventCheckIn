# Read First names, Last names and emails into a dictionary
# Go through the list and generate QR codes, appending them to dictionary entries
# Write dictionary to CSV
import csv
import qrcode

def parseContactList(list):
    parsedList = []

    with open('Test Email List.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        line_count = 0
        for row in csv_reader:
            if line_count == 0:
                print(f'Column names are {", ".join(row)}')
                line_count += 1
            parsedList.append(row)
        print(f'Processed {line_count} lines.')    

    return parsedList

def generateQRCode(content):
    img = qrcode.make(content)
    return img

def encodeEmails(list):
    for attendee in list:
        qrcode = generateQRCode(attendee["Email"])
        qrcode.save(f'./codes/{attendee["First Name"]}QRCode', 'png')
        attendee["QR Code"] = f'{attendee["First Name"]}QRCode.png'

attendees = parseContactList('Test Email List.csv')

encodeEmails(attendees)

for attendee in attendees:
    print(f'\t{attendee["First Name"]} {attendee["Last Name"]}\'s email address is {attendee["Email"]}.')
    print(f'\t\t{attendee["First Name"]} {attendee["Last Name"]}\'s QR Code is located at {attendee["QR Code"]}')

# with open('Test Email List.csv', mode='w') as csv_file:
#     fieldnames = ['Email', 'First Name', 'Last Name', 'QR Code']
#     writer = csv.DictWriter(csv_file, fieldnames=fieldnames)

#     writer.writeheader()
#     writer.writerow({'emp_name': 'John Smith', 'dept': 'Accounting', 'birth_month': 'November'})
#     writer.writerow({'emp_name': 'Erica Meyers', 'dept': 'IT', 'birth_month': 'March'})

