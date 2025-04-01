#Unique user worked

import requests
import tempfile
import uuid
import os
import time
from selenium import webdriver
from selenium.webdriver.chrome.options import Options

SERVER = "demo-app"
URL = "http://%s:8080/demo" % SERVER

def before_all(context):
    context.driver = __open_browser(context)

def __open_browser(context):
    # Get the chromedriver path from context userdata if provided.
    chrm = context.config.userdata.get('chromedriver_path', None)
    chrome_options = Options()
    
    # Create a unique temporary directory for Chrome's user data.
    unique_id = f"{uuid.uuid4()}_{int(time.time())}"
    unique_profile = os.path.join(tempfile.gettempdir(), f"chrome_profile_{unique_id}")
    
    # Store the profile directory in context for potential cleanup later
    context.chrome_profile_dir = unique_profile
    
    chrome_options.add_argument(f'--user-data-dir={unique_profile}')
    
    # Additional options to improve stability in CI environments.
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.add_argument('--headless')  # Run in headless mode for CI environments
    
    try:
        if chrm:
            return webdriver.Chrome(options=chrome_options, executable_path=chrm)
        else:
            return webdriver.Chrome(options=chrome_options)
    except Exception as e:
        print(f"Error launching Chrome: {e}")
        raise

def before_scenario(context, scenario):
    __reset_database()

def after_all(context):
    __close_browser(context)

def __close_browser(context):
    if hasattr(context, 'driver'):
        context.driver.quit()
    
    # Clean up the temporary directory
    if hasattr(context, 'chrome_profile_dir') and os.path.exists(context.chrome_profile_dir):
        try:
            import shutil
            shutil.rmtree(context.chrome_profile_dir)
        except Exception as e:
            print(f"Warning: Could not clean up Chrome profile directory: {e}")

def __reset_database():
    # Reset the database using the demo app URL.
    requests.get("%s/flyway" % URL)