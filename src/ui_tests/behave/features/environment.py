import requests
from selenium import webdriver
from selenium.webdriver.common.proxy import Proxy, ProxyType

# Update the server to match the demo app host and include the context path
SERVER = "demo-app"
URL = "http://%s:8080/demo" % SERVER

def before_all(context):
    __open_browser(context)

def __open_browser(context):
    chrm = context.config.userdata['chromedriver_path']
    
    try:
        # If there is a proxy, we'll use it. Otherwise, we won't.
        # Changed the proxy check URL to demo-app as well.
        requests.get("http://demo-app:8888", timeout=0.01)

        # If the proxy is available, use it.
        PROXY = "demo-app:8888"
        proxy = Proxy()
        proxy.proxy_type = ProxyType.MANUAL
        proxy.http_proxy = PROXY

        capabilities = webdriver.DesiredCapabilities.CHROME
        proxy.add_to_capabilities(capabilities)
        
        if chrm:
            context.driver = webdriver.Chrome(desired_capabilities=capabilities, executable_path=chrm)
        else:
            context.driver = webdriver.Chrome(desired_capabilities=capabilities)
        return context.driver
    except:
        # If proxy is not available, launch Chrome normally.
        if chrm:
            context.driver = webdriver.Chrome(executable_path=chrm)
        else:
            context.driver = webdriver.Chrome()
        return context.driver

def before_scenario(context, scenario):
    __reset_database()

def after_all(context):
    __close_browser(context)

def __close_browser(context):
    context.driver.close()

def __reset_database():
    # Reset the database using the demo app URL (the /flyway endpoint is now relative to the demo context)
    requests.get("%s/flyway" % URL)
