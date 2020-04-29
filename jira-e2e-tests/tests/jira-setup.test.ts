
import webdriver from 'selenium-webdriver';
import { Builder, ThenableWebDriver, WebElement, By, until, WebElementPromise, WebElementCondition } from 'selenium-webdriver';
import * as jira from '../lib/jira';

const base = 'http://localhost:2990/jira';
const welcome = base+'/secure/WelcomeToJIRA.jspa'

describe('webdriver', () => {
    let driver: ThenableWebDriver;

    beforeAll(async () => {
        driver = new webdriver.Builder()
            .forBrowser('chrome')
            .build();
    }, 30000);

    afterAll(async () => {
        await driver.quit();
    }, 40000);


    it('Can setup Jira', async () => {
        await jira.login(driver);
        await jira.setup(driver);

        await driver.sleep(10000);
    });

});
