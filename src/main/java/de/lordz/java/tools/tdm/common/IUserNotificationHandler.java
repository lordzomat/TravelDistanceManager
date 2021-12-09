package de.lordz.java.tools.tdm.common;

import java.awt.Component;

/**
 * Requirements for a user notification handler.
 * 
 * @author lordzomat
 *
 */
public interface IUserNotificationHandler {

    /**
     * Shows a message to the user.
     * 
     * @param message The message to show.
     */
    public void showErrorMessage(String message);
    
    /**
     * Shows a message to the user.
     * 
     * @param component The component in which the message is displayed.
     * @param message The message to show.
     */
    public void showErrorMessage(Component component, String message);
    
    /**
     * Shows a message to the user.
     * 
     * @param message The message to show.
     * @param title The title of the message window.
     */
    public void showErrorMessage(String message, String title);

    /**
     * Shows a message to the user.
     * 
     * @param component The component in which the message is displayed.
     * @param message The message to show.
     * @param title The title of the message window.
     */
    public void showErrorMessage(Component component, String message, String title);    
    
    /**
     * Shows a message to the user.
     * 
     * @param message The message to show.
     * @param title The title of the message window.
     * @param messageType The message type.
     */
    public void showMessage(String message, String title, int messageType);

    /**
     * Shows a message to the user.
     * 
     * @param component The component in which the message is displayed.
     * @param message The message to show.
     * @param title The title of the message window.
     * @param messageType The message type.
     */
    public void showMessage(Component component, String message, String title, int messageType);
       
    /**
     * Asks the user for confirmation for a given operation with yes/no option.
     * 
     * @param message The message to display.
     * @param title The title of the message window.
     * @return Returns <CODE>true</CODE> if user has confirmed the operation.
     */
    public boolean askForConfirmation(String message, String title);

    /**
     * Asks the user for confirmation for a given operation with yes/no option.
     * 
     * @param component The component in which the message is displayed.
     * @param message The message to display.
     * @param title The title of the message window.
     * @return Returns <CODE>true</CODE> if user has confirmed the operation.
     */
    public boolean askForConfirmation(Component component, String message, String title);
}
