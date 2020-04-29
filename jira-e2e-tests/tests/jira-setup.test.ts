
import webdriver from 'selenium-webdriver';
import { Builder, ThenableWebDriver, WebElement, By, until, WebElementPromise, WebElementCondition } from 'selenium-webdriver';

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
        await driver.navigate().to(base);

        await Promise.all([
            driver.findElement(By.id("login-form-username"))
                .then((e: WebElementPromise) => e.sendKeys("admin")),
            driver.findElement(By.id("login-form-password"))
                .then((e: WebElementPromise) => e.sendKeys("admin")),
        ]);
        await driver.findElement(By.id("login"))
            .then((e: WebElementPromise) => e.click());


        // Language
        await driver.wait(until.titleContains("Welcome to Jira"));
        await driver.findElement(By.id("next"))
            .then((e: WebElementPromise) => e.click());

        // Avatar
        await driver.wait(until.elementLocated(By.className("avatar-picker-section")));
        await driver.findElement(By.className("avatar-picker-done"))
            .then((e: WebElementPromise) => e.click());

        // Create sample project
        await driver.wait(until.elementLocated(By.id("sampleData")));
        await driver.findElement(By.id("sampleData"))
            .then((e: WebElementPromise) => e.click());

        await driver.wait(until.elementLocated(By.id("project-template-group-business")));
        await driver.findElement(By.className("create-project-dialog-create-button"))
            .then((e: WebElementPromise) => e.click());

        await driver.wait(until.elementLocated(By.id("name")))
            .then((e: WebElementPromise) => e.sendKeys("Test"));
        await driver.findElement(By.className("add-project-dialog-create-button"))
            .then((e: WebElementPromise) => e.click());

        await driver.sleep(10000);
    });

});
