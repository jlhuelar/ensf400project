import requests
import tempfile
from selenium import webdriver

SERVER = "demo-app"
URL = f"http://{SERVER}:8080/demo"

def before_all(context):
    # Create a unique temporary directory for the Chrome user data
    user_data_dir = tempfile.mkdtemp()
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument(f"--user-data-dir={user_data_dir}")
    
    # Optionally, use a custom chromedriver path if provided in userdata
    chrm = context.config.userdata.get('chromedriver_path', None)
    if chrm:
        context.driver = webdriver.Chrome(executable_path=chrm, options=chrome_options)
    else:
        context.driver = webdriver.Chrome(options=chrome_options)

def before_scenario(context, scenario):
    __reset_database()

def after_all(context):
    __close_browser(context)

def __close_browser(context):
    if hasattr(context, 'driver'):
        context.driver.quit()

def __reset_database():
    requests.get(f"{URL}/flyway")
