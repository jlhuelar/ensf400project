from behave import given, when, then
from hamcrest import *
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

empty_database = ''
one_user_registered = 'alice password123'
URL = 'http://demo-app:8080/demo/library.html'
DEFAULT_USERNAME = 'alice'
DEFAULT_PASSWORD = 'asdfkljhasdfishdfksaljdfh'

@given('I am not registered')
def step_impl(context):
    # Navigate to the page to ensure fresh state
    context.driver.get(URL)
    # Wait for page to load
    time.sleep(1)

@when('I register with a valid username and password')
def step_impl(context):
    __register_user(context, DEFAULT_USERNAME, DEFAULT_PASSWORD)

@then('it indicates I am successfully registered')
def step_impl(context):
    # Use updated finder method and wait for the element to be present
    wait = WebDriverWait(context.driver, 10)
    result = wait.until(EC.presence_of_element_located((By.ID, 'result')))
    assert_that(result.text, contains_string('successfully registered: true'))

@given('I am registered as a user')
def step_impl(context):
    context.username = DEFAULT_USERNAME
    context.password = DEFAULT_PASSWORD
    # Navigate to the page to ensure fresh state
    context.driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    __register_user(context, context.username, context.password)

@when('I login')
def step_impl(context):
    __login_user(context, context.username, context.password)

@then('the system allows secure access')
def step_impl(context):
    # Use updated finder method and wait for the element to be present
    wait = WebDriverWait(context.driver, 10)
    result = wait.until(EC.presence_of_element_located((By.ID, 'result')))
    assert_that(result.text, contains_string('access granted'))

def __register_user(context, username_text, password_text):
    driver = context.driver
    # Make sure we're on the right page
    driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    
    # Use the updated finder methods
    username = driver.find_element(By.ID, "register_username")
    username.clear()
    username.send_keys(username_text)
    
    password = driver.find_element(By.ID, "register_password")
    password.clear()
    password.send_keys(password_text)
    
    submit_button = driver.find_element(By.ID, "register_submit")
    submit_button.click()
    
    # Add a short wait to ensure form submission completes
    time.sleep(1)

def __login_user(context, username_text, password_text):
    driver = context.driver
    # Make sure we're on the right page
    driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    
    # Use the updated finder methods
    username = driver.find_element(By.ID, "login_username")
    username.clear()
    username.send_keys(username_text)
    
    password = driver.find_element(By.ID, "login_password")
    password.clear()
    password.send_keys(password_text)
    
    submit_button = driver.find_element(By.ID, "login_submit")
    submit_button.click()
    
    # Add a short wait to ensure form submission completes
    time.sleep(1)