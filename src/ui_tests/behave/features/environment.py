import requests
from selenium import webdriver

SERVER = "demo-app"
URL = "http://%s:8080/demo" % SERVER

def before_all(context):
    context.driver = __open_browser(context)

def __open_browser(context):
    chrm = context.config.userdata.get('chromedriver_path', None)
    # Launch Chrome without any proxy settings
    if chrm:
        return webdriver.Chrome(executable_path=chrm)
    else:
        return webdriver.Chrome()

def before_scenario(context, scenario):
    __reset_database()

def after_all(context):
    __close_browser(context)

def __close_browser(context):
    if hasattr(context, 'driver'):
        context.driver.quit()

def __reset_database():
    requests.get("%s/flyway" % URL)
