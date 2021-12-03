package de.lordz.java.tools.tdm;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public class ThemeMenuItem extends JMenuItem {

    private static final long serialVersionUID = -6224480312239183067L;
    private String themeClassName;

    /**
     * Creates a <code>ThemeMenuItem</code> with the specified text and theme class
     * name.
     *
     * @param text           the text of the <code>ThemeMenuItem</code>
     * @param themeClassName the name of the theme class of the
     *                       <code>ThemeMenuItem</code>
     */
    public ThemeMenuItem(String text, String themeClassName) {
        super(text, (Icon) null);
        this.themeClassName = themeClassName;
    }

    public String getThemeName() {
        return this.themeClassName;
    }
}
