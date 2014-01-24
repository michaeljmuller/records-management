/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po;

import org.alfresco.po.rm.FilePlanPage;
import org.alfresco.po.rm.RMConsolePage;
import org.alfresco.po.rm.RMDashBoardPage;
import org.alfresco.po.rm.RMSiteMembersPage;
import org.alfresco.po.rm.RecordSearchPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.ShareErrorPopup;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Extends the {@link FactorySharePage} for RM specific methods
 *
 * @author Tuna Aksoy
 * @since 2.2
 */
public class RMFactoryPage extends FactorySharePage
{
    public RMFactoryPage()
    {
        super();
        pages.put("rm-site-members", RMSiteMembersPage.class);
        pages.put("rm-console", RMConsolePage.class);
        pages.put("rm-rmsearch", RecordSearchPage.class);
        pages.put("rm-dashboard", RMDashBoardPage.class);
        pages.put("rm-documentlibrary", FilePlanPage.class);
        pages.put("rm-folder-rules", ManageRulesPage.class);
    }

    public HtmlPage getPage(WebDrone drone)
    {
        return RMFactoryPage.resolvePage(drone);
    }

    /**
     * Creates the appropriate page object based on the current page the
     * {@link WebDrone} is on.
     * 
     * @param drone WebDrone Alfresco unmanned web browser client
     * @return SharePage the page object response
     * @throws PageException
     */
    public static HtmlPage resolvePage(final WebDrone drone) throws PageException
    {
        // Determine if user is logged in if not return login page
        if (drone.getTitle().toLowerCase().contains("login"))
        {
            return new LoginPage(drone);
        }
        else
        {
            try
            {
                WebElement errorPrompt = drone.find(By.cssSelector(FAILURE_PROMPT));
                if(errorPrompt.isDisplayed())
                {
                    return new ShareErrorPopup(drone);
                }
            }
            catch(NoSuchElementException nse){ }

            // Determine what page we're on based on url
            return RMFactoryPage.getPage(drone.getCurrentUrl(), drone);
        }
    }

    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the drone is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     * 
     * @param driver WebDriver browser client
     * @return SharePage page object
     */
    public static SharePage getPage(final String url, WebDrone drone)
    {
        String pageName = RMFactoryPage.resolvePage(url);
        return instantiatePage(drone, pages.get(pageName));
    }

    /**
     * Extracts the String value from the last occurrence of slash in the url.
     *
     * @param url String url.
     * @return String page title
     */
    protected static String resolvePage(String url)
    {
        if (StringUtils.isBlank(url))
        {
            throw new UnsupportedOperationException("Empty url is not allowed");
        }

        if (url.endsWith("dashboard") && url.contains("site/rm"))
        {
            return "rm-dashboard";
        }

        //Check if rm based url
        if(url.contains("site/rm"))
        {
            String val[] = url.split("site/rm/");
            return String.format("rm-%s",val[1]);
        }

        return FactorySharePage.resolvePage(url);
    }
}
