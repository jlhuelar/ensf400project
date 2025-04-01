#Passed library Tests

from behave import given, when, then
from hamcrest import *
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

URL = 'http://demo-app:8080/demo/library.html'

@given('a borrower is registered')
def step_impl(context):
    borrower_name = "some borrower"
    __register_borrower(context, borrower_name)
    context.my_borrower_name = borrower_name
    # Wait to ensure the borrower registration is processed
    time.sleep(2)

@given('a book is available for borrowing')
def step_impl(context):
    book_title = "some book"
    __register_book(context, book_title)
    context.my_book_title = book_title
    # Wait to ensure the book registration is processed
    time.sleep(2)

@when('they try to check out the book')
def step_impl(context):
    __lend_book(context, context.my_borrower_name, context.my_book_title)
    # Wait to ensure checkout is processed
    time.sleep(2)

@then('the system indicates success')
def step_impl(context):
    # Use the updated finder method and wait for the element to be present
    wait = WebDriverWait(context.driver, 10)
    result = wait.until(EC.presence_of_element_located((By.ID, 'result')))
    assert_that(result.text, contains_string('SUCCESS'))

def __register_borrower(context, borrower_name):
    driver = context.driver
    driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    
    # Use the updated finder methods
    borrower = driver.find_element(By.ID, "register_borrower")
    borrower.clear()
    borrower.send_keys(borrower_name)
    
    submit_button = driver.find_element(By.ID, "register_borrower_submit")
    submit_button.click()
    
    # Wait for the registration to complete
    time.sleep(1)

def __register_book(context, book_title):
    driver = context.driver
    driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    
    # Use the updated finder methods
    book = driver.find_element(By.ID, "register_book")
    book.clear()
    book.send_keys(book_title)
    
    submit_button = driver.find_element(By.ID, "register_book_submit")
    submit_button.click()
    
    # Wait for the registration to complete
    time.sleep(1)

def __lend_book(context, my_borrower_name, my_book_title):
    driver = context.driver
    driver.get(URL)
    # Wait for page to load
    time.sleep(1)
    
    # Looking at the JS code, there might be autocomplete/dropdown functionality
    # that we need to handle differently. Let's try the standard approach first.
    
    try:
        # Check if we have a standard input field or a select dropdown for books
        book_input = driver.find_element(By.ID, "lend_book")
        # If it's a select element, we need to handle differently
        if book_input.tag_name.lower() == 'select':
            from selenium.webdriver.support.ui import Select
            book_select = Select(book_input)
            book_select.select_by_visible_text(my_book_title)
        else:
            # Clear any existing text and add our book title
            book_input.clear()
            book_input.send_keys(my_book_title)
            # If there's an autocomplete, we might need to click on a result
            time.sleep(1)
            # Look for any autocomplete items that match our book title
            try:
                autocomplete_items = driver.find_elements(By.XPATH, f"//li[contains(text(), '{my_book_title}')]")
                if autocomplete_items and len(autocomplete_items) > 0:
                    autocomplete_items[0].click()
            except:
                # Continue if no autocomplete items found
                pass
    except:
        # Handle any exceptions and continue
        print("Error handling book input field")

    try:
        # Same approach for the borrower field
        borrower_input = driver.find_element(By.ID, "lend_borrower")
        # If it's a select element, we need to handle differently
        if borrower_input.tag_name.lower() == 'select':
            from selenium.webdriver.support.ui import Select
            borrower_select = Select(borrower_input)
            borrower_select.select_by_visible_text(my_borrower_name)
        else:
            # Clear any existing text and add our borrower name
            borrower_input.clear()
            borrower_input.send_keys(my_borrower_name)
            # If there's an autocomplete, we might need to click on a result
            time.sleep(1)
            # Look for any autocomplete items that match our borrower name
            try:
                autocomplete_items = driver.find_elements(By.XPATH, f"//li[contains(text(), '{my_borrower_name}')]")
                if autocomplete_items and len(autocomplete_items) > 0:
                    autocomplete_items[0].click()
            except:
                # Continue if no autocomplete items found
                pass
    except:
        # Handle any exceptions and continue
        print("Error handling borrower input field")
    
    # Wait to ensure both fields are properly filled
    time.sleep(1)
    
    # Click the submit button
    submit_button = driver.find_element(By.ID, "lend_book_submit")
    submit_button.click()
    
    # Wait for the lending operation to complete
    time.sleep(2)