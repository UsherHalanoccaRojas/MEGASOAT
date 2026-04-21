
package Utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;

public class FileDragAndDrop {
    public FileDragAndDrop(JComponent component, Consumer<File[]> onFilesDropped) {
        new DropTarget(component, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    java.awt.datatransfer.Transferable tr = dtde.getTransferable();
                    if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
                        List<File> files = (List<File>) tr.getTransferData(
                            java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                        onFilesDropped.accept(files.toArray(new File[0]));
                        dtde.dropComplete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });
    }
}