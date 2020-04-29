
import webdriver from 'selenium-webdriver';
import { Builder, ThenableWebDriver, WebElement, By, until, WebElementPromise, WebElementCondition } from 'selenium-webdriver';

export const baseURL = 'http://localhost:2990/jira';
export const welcomeURL = baseURL+'/secure/WelcomeToJIRA.jspa'
export const loginURL = baseURL+'/login.jsp';

export const login = async (driver: ThenableWebDriver): Promise<void> => {
    await driver.navigate().to(loginURL);

    await Promise.all([
        driver.findElement(By.id("login-form-username"))
            .then((e: WebElementPromise) => e.sendKeys("admin")),
        driver.findElement(By.id("login-form-password"))
            .then((e: WebElementPromise) => e.sendKeys("admin")),
    ]);

    await driver.findElement(By.id("login-form-submit"))
        .then((e: WebElementPromise) => e.click());
};

export const setup = async (driver: ThenableWebDriver): Promise<void> => {
    if (await driver.getCurrentUrl() !== welcomeURL) {
        console.log("Not on welcome page; skipping setup");
        return;
    }

    // FIXME: Each of these should be conditional to handle partial
    // setups.

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
    const button = await driver.findElement(By.className("add-project-dialog-create-button"));
    await driver.wait(until.elementIsEnabled(button))
        .then((e: WebElementPromise) => e.click());

}
