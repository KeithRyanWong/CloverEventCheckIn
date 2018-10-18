from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
import requests
import time

sandbox_host = 'sandbox.dev.clover.com'
prod_host = 'clover.com'
app_id = 'B8KAN7XASTP4M'
merchant_email = "crocinusluxfiat@gmail.com"
merchant_pwd = "Letmein!"
merchant_name = "Kwover2"

chrome_options = webdriver.ChromeOptions()
chrome_options.add_argument('--incognito')
driver = webdriver.Chrome("./chromedriver", chrome_options=chrome_options)
driver.set_page_load_timeout(30)
driver.get(f'https://sandbox.dev.clover.com/oauth/authorize?client_id={app_id}')
driver.find_element_by_id("email").send_keys(merchant_email)
driver.find_element_by_id("password").send_keys(merchant_pwd)
driver.find_element_by_id("login-submit").click()
if(driver.current_url == f'https://sandbox.dev.clover.com/oauth/authorize?client_id={app_id}'):
    driver.find_element_by_xpath('//a[text()="Kwover2"]').click()

print(driver.current_url)
time.sleep(10)

driver.quit()