package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

abstract class BaseComponent(page: Page, root: Locator) : UiObject(page, root)
