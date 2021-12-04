package de.lordz.java.tools.tdm;

import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.lordz.java.tools.tdm.common.Logger;

public class EntityTextChangeDocumentListener implements DocumentListener {
    private Consumer<String> consumer;

    public EntityTextChangeDocumentListener(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void insertUpdate(DocumentEvent e) {
        processUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        processUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        processUpdate(e);
    }

    private void processUpdate(DocumentEvent e) {
        var document = e.getDocument();
        try {
            this.consumer.accept(document.getText(0, document.getLength()));
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }
}