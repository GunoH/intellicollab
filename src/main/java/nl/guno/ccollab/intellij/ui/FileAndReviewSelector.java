package nl.guno.ccollab.intellij.ui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.CheckBoxListListener;
import com.intellij.ui.ListCellRendererWrapper;
import com.smartbear.ccollab.datamodel.Review;
import nl.guno.ccollab.intellij.MessageResources;

public class FileAndReviewSelector extends DialogWrapper implements CheckBoxListListener {
    private JPanel contentPane;
    private JPanel headerPane;
    private JPanel checkboxListPane;
    private CheckBoxList fileCheckBoxList;
    private JPanel reviewPane;
    private JComboBox reviewComboBox;

    private final List<Pair<File, Boolean>> initialFileList;
    private List<Pair<File, Boolean>> workingFileList;
    private DefaultListModel fileListModel;

    private final List<Review> reviewList;
    private DefaultComboBoxModel reviewComboBoxModel;
    private String preselectedReviewName;

    public FileAndReviewSelector(List<Pair<File, Boolean>> fileList, @NotNull Review[] reviewList, Project project,
                                 String preselectedReviewName) {

        super(project);

        init();


        initialFileList = fileList;

        this.reviewList = new ArrayList<>();
        this.reviewList.addAll(Arrays.asList(reviewList));
        this.preselectedReviewName = preselectedReviewName;

        // Sort the list of reviews in descending order of last activity date.
        Collections.sort(this.reviewList, (r1, r2) -> r2.getCreationDate().compareTo(r1.getCreationDate()));

        reset();

        setTitle(MessageResources.message("dialog.selectFilesForReview.title"));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return reviewComboBox;
    }

    private void createUIComponents() {
        fileListModel = new DefaultListModel();
        fileCheckBoxList = new CheckBoxList(fileListModel, this);

        reviewComboBoxModel = new DefaultComboBoxModel();
        reviewComboBox = new ComboBox(reviewComboBoxModel);
        reviewComboBox.setRenderer(new ListCellRendererWrapper<Review>() {
            @Override
            public void customize(JList list, Review review, int index, boolean isSelected, boolean hasFocus) {

                if (isSelected) {
                    //noinspection UseJBColor
                    setBackground(Color.BLUE);
                } else {
                    setBackground(list.getBackground());
                }

                setText(review.getId() + " " + review.getTitle());
            }
        });
    }

    void reset() {
        workingFileList = new ArrayList<>(initialFileList);
        update();
    }

    void update() {
        fileListModel.clear();
        for (Pair<File, Boolean> pair : workingFileList) {
            fileListModel.addElement(createCheckBox(pair.first, pair.second));
        }

        reviewComboBoxModel.removeAllElements();
        for (Review review : reviewList) {
            reviewComboBoxModel.addElement(review);

            if (preselectedReviewName != null && review.getTitle().equals(preselectedReviewName)) {
                reviewComboBoxModel.setSelectedItem(review);
            }
         }
    }

    JCheckBox createCheckBox(File file, boolean checked) {
        return new JCheckBox(FileUtil.toSystemDependentName(file.getPath()), checked);
    }

    @Override
    public void checkBoxSelectionChanged(int index, boolean value) {
        final Pair<File, Boolean> pair = workingFileList.remove(index);
        workingFileList.add(index, Pair.create(pair.first, value));
    }

    public File[] retrieveSelectedFiles() {
        List<File> result = new ArrayList<>();
        for (Pair<File, Boolean> pair : workingFileList) {
            if (pair.second) {
                result.add(pair.first);
            }
        }

        return result.toArray(new File[result.size()]);
    }

    public Integer getSelectedReviewId() {
        return ((Review)reviewComboBox.getSelectedItem()).getId();
    }
}

