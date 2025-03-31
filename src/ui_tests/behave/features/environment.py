import requests
import tempfile
from selenium import webdriver
from selenium.webdriver.chrome.options import Options

SERVER = "demo-app"
URL = "http://%s:8080/demo" % SERVER

def before_all(context):
    context.driver = __open_browser(context)

def __open_browser(context):
    # Get the chromedriver path from context userdata if provided
    chrm = context.config.userdata.get('chromedriver_path', None)
    
    # Set up Chrome options and create a unique temporary user data directory.
    chrome_options = Options()
    unique_profile = tempfile.mkdtemp()
    chrome_options.add_argument(f'--user-data-dir={unique_profile}')
    
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

def __reset_database():
    requests.get("%s/flyway" % URL)
