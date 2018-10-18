import requests
import logging
import json
import os
import shutil

env = 'https://apisandbox.dev.clover.com'
mid = 'J96JE3HEHBDG1'
token = input('Token pls: ')

#delete customers
def deleteCustomer(id):
    request_url = env + "/v3/merchants/" + mid + '/customers/' + id
    max_attempts = 10
    attempts = 0
    # Retry attempts should max out after a reasonable number of attemps.

    while attempts < max_attempts:  
        # Make a request to Clover REST API
        response = requests.delete(request_url, headers={'Authorization': 'Bearer ' + token})
    
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

request_url = env + "/v3/merchants/" + mid + '/customers?limit=1000'

r = requests.get(request_url, headers={'Authorization': 'Bearer ' + token})

for customer in r.json()["elements"]:
    print("deleting customer id: " + customer["id"])
    deleteCustomer(customer["id"])


#delete the QR codes
folder = '../codes'
for the_file in os.listdir(folder):
    file_path = os.path.join(folder, the_file)
    try:
        if os.path.isfile(file_path):
            os.unlink(file_path)
    except Exception as e:
        print(e)

print("done!")