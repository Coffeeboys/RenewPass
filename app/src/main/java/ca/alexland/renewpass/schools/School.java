package ca.alexland.renewpass.schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;

import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;

/**
 * Created by AlexLand on 2015-12-30.
 */
public interface School {
    HtmlDocument login(HtmlDocument authPage, String username, String password) throws SchoolAuthenticationFailedException;
    String getID();
}
