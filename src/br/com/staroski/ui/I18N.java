package br.com.staroski.ui;

import java.util.Locale;

/**
 * Interface for graphic user interfaces that have multilanguage support.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface I18N {

    /**
     * Called when the user interface's {@link Locale} is changed.
     * 
     * @param newLocale The new {@link Locale} to apply to the user interface.
     */
    public void onLocaleChange(Locale newLocale);
}
