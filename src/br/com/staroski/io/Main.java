package br.com.staroski.io;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        for (String key : keys()) {
            Object value = UIManager.get(key);
            System.out.println(key + "=" + (value == null ? "" : value));
        }
    }

    static Set<String> keys() {
        return new TreeSet<>(
                Arrays.asList(
                        "FileChooser.acceptAllFileFilterText",
                        "FileChooser.ancestorInputMap",
                        "FileChooser.byDateText",
                        "FileChooser.byNameText",
                        "FileChooser.cancelButtonMnemonic",
                        "FileChooser.cancelButtonText",
                        "FileChooser.chooseButtonText",
                        "FileChooser.createButtonText",
                        "FileChooser.desktopName",
                        "FileChooser.detailsViewIcon",
                        "FileChooser.directoryDescriptionText",
                        "FileChooser.directoryOpenButtonMnemonic",
                        "FileChooser.directoryOpenButtonText",
                        "FileChooser.fileDescriptionText",
                        "FileChooser.fileNameLabelMnemonic",
                        "FileChooser.fileNameLabelText",
                        "FileChooser.fileSizeGigaBytes",
                        "FileChooser.fileSizeKiloBytes",
                        "FileChooser.fileSizeMegaBytes",
                        "FileChooser.filesOfTypeLabelMnemonic",
                        "FileChooser.filesOfTypeLabelText",
                        "FileChooser.helpButtonMnemonic",
                        "FileChooser.helpButtonText",
                        "FileChooser.homeFolderIcon",
                        "FileChooser.listViewIcon",
                        "FileChooser.lookInLabelMnemonic",
                        "FileChooser.mac.newFolder",
                        "FileChooser.mac.newFolder.subsequent",
                        "FileChooser.newFolderAccessibleName",
                        "FileChooser.newFolderButtonText",
                        "FileChooser.newFolderErrorSeparator",
                        "FileChooser.newFolderErrorText",
                        "FileChooser.newFolderExistsErrorText",
                        "FileChooser.newFolderIcon",
                        "FileChooser.newFolderPromptText",
                        "FileChooser.newFolderTitleText",
                        "FileChooser.openButtonMnemonic",
                        "FileChooser.openButtonText",
                        "FileChooser.openDialogTitleText",
                        "FileChooser.openTitleText",
                        "FileChooser.readOnly",
                        "FileChooser.saveButtonMnemonic",
                        "FileChooser.saveButtonText",
                        "FileChooser.saveDialogFileNameLabelText",
                        "FileChooser.saveDialogTitleText",
                        "FileChooser.saveTitleText",
                        "FileChooser.untitledFileName",
                        "FileChooser.untitledFolderName",
                        "FileChooser.upFolderIcon",
                        "FileChooser.updateButtonMnemonic",
                        "FileChooser.updateButtonText",
                        "FileChooser.useSystemExtensionHiding",
                        "FileChooser.usesSingleFilePane",

                        "FileChooser.lookInLabelMnemonic",
                        "FileChooser.lookInLabelText",
                        "FileChooser.saveInLabelText",
                        "FileChooser.fileNameLabelMnemonic",
                        "FileChooser.fileNameLabelText",
                        "FileChooser.folderNameLabelMnemonic",
                        "FileChooser.folderNameLabelText",
                        "FileChooser.filesOfTypeLabelMnemonic",
                        "FileChooser.filesOfTypeLabelText",
                        "FileChooser.upFolderToolTipText",
                        "FileChooser.upFolderAccessibleName",
                        "FileChooser.newFolderToolTipText",
                        "FileChooser.newFolderAccessibleName",
                        "FileChooser.viewMenuButtonToolTipText",
                        "FileChooser.viewMenuButtonAccessibleName",

                        "FileChooser.lookInLabelMnemonic",
                        "FileChooser.lookInLabelText",
                        "FileChooser.saveInLabelText",

                        "FileChooser.fileNameLabelMnemonic",
                        "FileChooser.fileNameLabelText",
                        "FileChooser.folderNameLabelMnemonic",
                        "FileChooser.folderNameLabelText",

                        "FileChooser.filesOfTypeLabelMnemonic",
                        "FileChooser.filesOfTypeLabelText",

                        "FileChooser.upFolderToolTipText",
                        "FileChooser.upFolderAccessibleName",

                        "FileChooser.homeFolderToolTipText",
                        "FileChooser.homeFolderAccessibleName",

                        "FileChooser.newFolderToolTipText",
                        "FileChooser.newFolderAccessibleName",

                        "FileChooser.listViewButtonToolTipText",
                        "FileChooser.listViewButtonAccessibleName",

                        "FileChooser.detailsViewButtonToolTipText",
                        "FileChooser.detailsViewButtonAccessibleName",

                        "FileChooser.enterFolderNameLabelText",
                        "FileChooser.enterFolderNameLabelMnemonic",
                        "FileChooser.enterFileNameLabelText",
                        "FileChooser.enterFileNameLabelMnemonic",

                        "FileChooser.filesLabelText",
                        "FileChooser.filesLabelMnemonic",

                        "FileChooser.foldersLabelText",
                        "FileChooser.foldersLabelMnemonic",

                        "FileChooser.pathLabelText",
                        "FileChooser.pathLabelMnemonic",

                        "FileChooser.filterLabelText",
                        "FileChooser.filterLabelMnemonic",

                        "FileChooser.other.newFolder",
                        "FileChooser.other.newFolder.subsequent",

                        "FileChooser.win32.newFolder",
                        "FileChooser.win32.newFolder.subsequent",

                        "FileChooser.lookInLabelMnemonic",
                        "FileChooser.lookInLabelText",
                        "FileChooser.saveInLabelText",

                        "FileChooser.fileNameLabelMnemonic",
                        "FileChooser.fileNameLabelText",
                        "FileChooser.folderNameLabelMnemonic",
                        "FileChooser.folderNameLabelText",

                        "FileChooser.filesOfTypeLabelMnemonic",
                        "FileChooser.filesOfTypeLabelText",

                        "FileChooser.upFolderToolTipText",
                        "FileChooser.upFolderAccessibleName",

                        "FileChooser.homeFolderToolTipText",
                        "FileChooser.homeFolderAccessibleName",

                        "FileChooser.newFolderToolTipText",
                        "FileChooser.newFolderAccessibleName",

                        "FileChooser.listViewButtonToolTipText",
                        "FileChooser.listViewButtonAccessibleName",

                        "FileChooser.detailsViewButtonToolTipText",
                        "FileChooser.detailsViewButtonAccessibleName",

                        "FileChooser.listViewBorder",
                        "FileChooser.listViewBackground",
                        "FileChooser.listViewWindowsStyle",
                        "FileChooser.readOnly",

                        "FileChooser.viewMenuLabelText",
                        "FileChooser.refreshActionLabelText",
                        "FileChooser.newFolderActionLabelText",

                        "FileChooser.listViewActionLabelText",
                        "FileChooser.detailsViewActionLabelText",

                        "FileChooser.fileSizeKiloBytes",
                        "FileChooser.fileSizeMegaBytes",
                        "FileChooser.fileSizeGigaBytes",

                        "FileChooser.filesListAccessibleName",
                        "FileChooser.filesDetailsAccessibleName",

                        "FileChooser.renameErrorTitleText",
                        "FileChooser.renameErrorText",
                        "FileChooser.renameErrorFileExistsText",

                        "FileChooser.fileNameHeaderText",
                        "FileChooser.fileSizeHeaderText",
                        "FileChooser.fileDateHeaderText",

                        "FileView.fullRowSelection",

                        "OptionPane.background",
                        "OptionPane.border",
                        "OptionPane.buttonAreaBorder",
                        "OptionPane.buttonClickThreshhold",
                        "OptionPane.buttonFont",
                        "OptionPane.cancelButtonMnemonic",
                        "OptionPane.cancelButtonText",
                        "OptionPane.errorIcon",
                        "OptionPane.font",
                        "OptionPane.foreground",
                        "OptionPane.informationIcon",
                        "OptionPane.inputDialogTitle",
                        "OptionPane.messageAreaBorder",
                        "OptionPane.messageDialogTitle",
                        "OptionPane.messageFont",
                        "OptionPane.messageForeground",
                        "OptionPane.minimumSize",
                        "OptionPane.noButtonMnemonic",
                        "OptionPane.noButtonText",
                        "OptionPane.okButtonMnemonic",
                        "OptionPane.okButtonText",
                        "OptionPane.questionIcon",
                        "OptionPane.titleText",
                        "OptionPane.warningIcon",
                        "OptionPane.windowBindings",
                        "OptionPane.yesButtonMnemonic",
                        "OptionPane.yesButtonText"

                ));
    }
}