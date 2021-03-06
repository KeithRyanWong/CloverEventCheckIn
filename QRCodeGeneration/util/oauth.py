from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
import requests
import time
from urllib.parse import urlparse
from urllib.parse import parse_qs




def getAuthCode(merchant, email, pwd):
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--incognito')
    driver = webdriver.Chrome("./chromedriver", chrome_options=chrome_options)
    driver.set_page_load_timeout(30)
    driver.implicitly_wait(10)
    driver.get(f'https://sandbox.dev.clover.com/oauth/authorize?client_id={app_id}')
    driver.find_element_by_id("email").send_keys(email)
    driver.find_element_by_id("password").send_keys(pwd)
    driver.find_element_by_id("login-submit").click()
    if(driver.current_url == f'https://sandbox.dev.clover.com/oauth/authorize?client_id={app_id}'):
        driver.find_element_by_xpath(f'//a[text()={merchant}]').click()

    time.sleep(2)
    authURL = driver.current_url

    o = urlparse(authURL)
    qs = parse_qs(o[4])

    auth_code = qs["code"][0]
    mid = qs["merchant_id"][0]

    print(auth_code)
    driver.quit()

    return auth_code



