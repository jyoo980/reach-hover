package com.github.jyoo980.reachhover.ui

import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.hint.HintManagerImpl.ActionToIgnore
import com.intellij.codeInsight.hint.ImplementationViewComponent
import com.intellij.codeInsight.hint.ImplementationViewDocumentFactory
import com.intellij.codeInsight.hint.ImplementationViewElement
import com.intellij.codeInsight.hint.LanguageImplementationTextProcessor
import com.intellij.codeInsight.hint.LanguageImplementationTextSelectioner
import com.intellij.find.FindUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.IdeBundle
import com.intellij.ide.highlighter.HighlighterFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager
import com.intellij.openapi.fileEditor.impl.text.QuickDefinitionProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.NlsActions.ActionText
import com.intellij.openapi.util.NlsContexts.TabTitle
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ScreenUtil
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBScrollPane
import com.intellij.usages.UsageView
import com.intellij.util.DocumentUtil
import com.intellij.util.IconUtil
import com.intellij.util.PairFunction
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.util.ArrayList
import java.util.HashSet
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.*
import kotlin.jvm.Volatile
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.TestOnly

class ReachabilityViewComponent(
    elements: Collection<ImplementationViewElement>,
    index: Int,
    openUsageView: Consumer<ImplementationViewComponent>?
) : JPanel(BorderLayout()) {
    private val factory: EditorFactory
    private val project: Project?
    var elements: Array<ImplementationViewElement>? = null
        private set
    var index = 0
        private set
    private var myEditor: EditorEx

    @Volatile private var myEditorReleased = false

    @get:ApiStatus.Internal val viewingPanel: JPanel
    private val myBinarySwitch: CardLayout
    private val myBinaryPanel: JPanel

    @get:ApiStatus.Internal var fileChooserComboBox: ComboBox<FileDescriptor?>? = null
    private var myNonTextEditor: FileEditor? = null
    private var myCurrentNonTextEditorProvider: FileEditorProvider? = null
    private var myHint: JBPopup? = null
    private var myTitle: @TabTitle String? = null
    private val myToolbar: ActionToolbar

    @get:ApiStatus.Internal var singleEntryPanel: JPanel? = null
    fun setHint(hint: JBPopup?, title: @TabTitle String) {
        myHint = hint
        myTitle = title
    }

    fun hasElementsToShow(): Boolean = elements?.isNotEmpty() ?: false

    class FileDescriptor
    internal constructor(val myFile: VirtualFile, val myElement: ImplementationViewElement)

    init {
        project = if (elements.size > 0) elements.iterator().next().project else null
        factory = EditorFactory.getInstance()
        val doc = factory.createDocument("")
        doc.setReadOnly(true)
        myEditor = factory.createEditor(doc, project, EditorKind.PREVIEW) as EditorEx
        tuneEditor()
        myBinarySwitch = CardLayout()
        viewingPanel = JPanel(myBinarySwitch)
        viewingPanel.add(myEditor.component, TEXT_PAGE_KEY)
        myBinaryPanel = JPanel(BorderLayout())
        viewingPanel.add(myBinaryPanel, BINARY_PAGE_KEY)
        add(viewingPanel, BorderLayout.CENTER)
        myToolbar = createToolbar(openUsageView)
        preferredSize = JBUI.size(600, 400)
        update(elements) {
            psiElements: Array<ImplementationViewElement>,
            fileDescriptors: List<FileDescriptor?> ->
            if (psiElements.size == 0) return@update false
            this.elements = psiElements
            this.index = if (index < elements.size) index else 0
            val virtualFile: VirtualFile? = elements.toList()[this.index].containingFile
            tuneEditor(virtualFile)
            val toolbarPanel = JPanel(GridBagLayout())
            val gc =
                GridBagConstraints(
                    GridBagConstraints.RELATIVE,
                    0,
                    1,
                    1,
                    1.0,
                    0.0,
                    GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL,
                    JBUI.insets(0),
                    0,
                    0
                )
            singleEntryPanel = JPanel(BorderLayout())
            toolbarPanel.add(singleEntryPanel, gc)
            fileChooserComboBox = ComboBox(fileDescriptors.toTypedArray(), 250)
            fileChooserComboBox!!.isOpaque = false
            fileChooserComboBox!!.addActionListener {
                val index1 = fileChooserComboBox!!.selectedIndex
                if (this.index != index1) {
                    this.index = index1
                    updateControls()
                }
            }
            toolbarPanel.add(fileChooserComboBox, gc)
            if (elements.size > 1) {
                singleEntryPanel!!.isVisible = false
                updateRenderer(project)
            } else {
                fileChooserComboBox!!.isVisible = false
                if (virtualFile != null) {
                    updateSingleEntryLabel(virtualFile)
                }
            }
            gc.fill = GridBagConstraints.NONE
            gc.weightx = 0.0
            val component = myToolbar.component
            component.border = null
            toolbarPanel.add(component, gc)
            toolbarPanel.background = UIUtil.getToolTipActionBackground()
            toolbarPanel.border = JBUI.Borders.empty(3)
            toolbarPanel.isOpaque = false
            add(toolbarPanel, BorderLayout.NORTH)
            updateControls()
            true
        }
    }

    private fun createGearActionButton(
        openUsageView: Consumer<ImplementationViewComponent>?
    ): DefaultActionGroup {
        val gearActions: DefaultActionGroup =
            object : DefaultActionGroup() {
                override fun update(e: AnActionEvent) {
                    super.update(e)
                    e.presentation.icon = AllIcons.Actions.More
                    e.presentation.putClientProperty(
                        ActionButton.HIDE_DROPDOWN_ICON,
                        java.lang.Boolean.TRUE
                    )
                }
            }
        gearActions.isPopup = true
        val edit: EditSourceActionBase = EditSourceAction()
        edit.registerCustomShortcutSet(
            CompositeShortcutSet(CommonShortcuts.getEditSource(), CommonShortcuts.ENTER),
            this
        )
        gearActions.add(edit)
        if (openUsageView != null) {
            val icon =
                ToolWindowManager.getInstance(project!!)
                    .getLocationIcon(ToolWindowId.FIND, AllIcons.General.Pin_tab)
            gearActions.add(
                object :
                    AnAction(
                        Supplier { IdeBundle.message("show.in.find.window.button.name") },
                        icon
                    ) {
                    override fun actionPerformed(e: AnActionEvent) {
                        // Note: commented out because openUsageView accepts something of type
                        // ImplementationViewComponent
                        // openUsageView.accept(ImplementationViewComponent.this);
                        // if (myHint.isVisible()) {
                        //     myHint.cancel();
                        // }
                    }
                }
            )
        }
        return gearActions
    }

    private fun updateSingleEntryLabel(virtualFile: VirtualFile) {
        singleEntryPanel!!.removeAll()
        val label =
            JLabel(
                elements!![index].presentableText,
                getIconForFile(virtualFile, project),
                SwingConstants.LEFT
            )
        singleEntryPanel!!.add(label, BorderLayout.CENTER)
        label.foreground = FileStatusManager.getInstance(project!!).getStatus(virtualFile).color
        singleEntryPanel!!.add(
            JLabel(
                elements!![index].locationText,
                elements!![index].locationIcon,
                SwingConstants.LEFT
            ),
            BorderLayout.EAST
        )
        singleEntryPanel!!.isOpaque = false
        singleEntryPanel!!.isVisible = true
        singleEntryPanel!!.border = JBUI.Borders.empty(4, 3)
    }

    private fun tuneEditor(virtualFile: VirtualFile?) {
        if (virtualFile != null) {
            myEditor.highlighter = HighlighterFactory.createHighlighter(project, virtualFile)
        }
    }

    private fun tuneEditor() {
        val color =
            EditorColorsManager.getInstance()
                .globalScheme
                .getColor(EditorColors.DOCUMENTATION_COLOR)
        if (color != null) {
            myEditor.backgroundColor = color
        }
        val settings = myEditor.settings
        settings.additionalLinesCount = 1
        settings.additionalColumnsCount = 1
        settings.isLineMarkerAreaShown = false
        settings.isIndentGuidesShown = false
        settings.isLineNumbersShown = true
        settings.isFoldingOutlineShown = false
        settings.isCaretRowShown = false
        myEditor.setBorder(JBUI.Borders.empty(12, 6))
        myEditor.scrollPane.viewportBorder = JBScrollPane.createIndentBorder()
    }

    private fun updateRenderer(project: Project?) {
        fileChooserComboBox!!.renderer = createRenderer(project)
    }

    @get:TestOnly
    val visibleFiles: Array<String?>
        get() {
            val model = fileChooserComboBox!!.model
            val result = arrayOfNulls<String>(model.size)
            for (i in 0 until model.size) {
                val o = model.getElementAt(i)
                result[i] = o!!.myElement.presentableText
            }
            return result
        }

    val preferredFocusableComponent: JComponent?
        get() = if (elements!!.size > 1) fileChooserComboBox else myEditor.contentComponent

    private fun updateControls() {
        updateCombo()
        updateEditorText()
        myToolbar.updateActionsImmediately()
    }

    private fun updateCombo() {
        if (fileChooserComboBox != null && fileChooserComboBox!!.isVisible) {
            fileChooserComboBox!!.selectedIndex = index
        }
    }

    private fun updateEditorText() {
        disposeNonTextEditor()
        val foundElement = elements!![index]
        val project = foundElement.project
        val vFile = foundElement.containingFile ?: return
        for (documentFactory in ImplementationViewDocumentFactory.EP_NAME.extensions) {
            val document = documentFactory.createDocument(foundElement)
            if (document != null) {
                replaceEditor(project, vFile, documentFactory, document)
                return
            }
        }
        val providers = FileEditorProviderManager.getInstance().getProviders(project, vFile)
        for (provider in providers) {
            if (provider is QuickDefinitionProvider) {
                updateTextElement(foundElement)
                myBinarySwitch.show(viewingPanel, TEXT_PAGE_KEY)
                break
            } else if (provider.accept(project, vFile)) {
                myCurrentNonTextEditorProvider = provider
                myNonTextEditor = myCurrentNonTextEditorProvider!!.createEditor(project, vFile)
                myBinaryPanel.removeAll()
                myBinaryPanel.add(myNonTextEditor!!.component)
                myBinarySwitch.show(viewingPanel, BINARY_PAGE_KEY)
                break
            }
        }
    }

    private fun replaceEditor(
        project: Project,
        vFile: VirtualFile,
        documentFactory: ImplementationViewDocumentFactory,
        document: Document
    ) {
        viewingPanel.remove(myEditor.component)
        factory.releaseEditor(myEditor)
        myEditor = factory.createEditor(document, project, EditorKind.PREVIEW) as EditorEx
        tuneEditor(vFile)
        documentFactory.tuneEditorBeforeShow(myEditor)
        viewingPanel.add(myEditor.component, TEXT_PAGE_KEY)
        myBinarySwitch.show(viewingPanel, TEXT_PAGE_KEY)
        documentFactory.tuneEditorAfterShow(myEditor)
    }

    private fun disposeNonTextEditor() {
        if (myNonTextEditor != null) {
            myCurrentNonTextEditorProvider!!.disposeEditor(myNonTextEditor!!)
            myNonTextEditor = null
            myCurrentNonTextEditorProvider = null
        }
    }

    private fun updateTextElement(elt: ImplementationViewElement) {
        val newText = elt.text
        if (newText == null || Comparing.strEqual(newText, myEditor.document.text)) return
        DocumentUtil.writeInRunUndoTransparentAction {
            val fragmentDoc: Document = myEditor.document
            fragmentDoc.setReadOnly(false)
            fragmentDoc.replaceString(0, fragmentDoc.textLength, newText)
            fragmentDoc.setReadOnly(true)
            val element = elt.elementForShowUsages
            val file = element?.containingFile
            myEditor.settings.setTabSize(
                if (file != null) CodeStyle.getIndentOptions(file).TAB_SIZE
                else CodeStyle.getSettings(elt.project).getTabSize(null)
            )
            myEditor.caretModel.moveToOffset(0)
            myEditor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
        }
    }

    override fun removeNotify() {
        super.removeNotify()
        if (ScreenUtil.isStandardAddRemoveNotify(this) && !myEditorReleased) {
            myEditorReleased = true // remove notify can be called several times for popup windows
            EditorFactory.getInstance().releaseEditor(myEditor)
            disposeNonTextEditor()
        }
    }

    private fun createToolbar(
        openUsageView: Consumer<ImplementationViewComponent>?
    ): ActionToolbar {
        val group = DefaultActionGroup()
        val back: BackAction = BackAction()
        back.registerCustomShortcutSet(
            CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
            this
        )
        group.add(back)
        group.add(
            object : ToolbarLabelAction() {
                override fun createCustomComponent(
                    presentation: Presentation,
                    place: String
                ): JComponent {
                    val component = super.createCustomComponent(presentation, place)
                    component.border = JBUI.Borders.empty(0, 2)
                    return component
                }

                override fun update(e: AnActionEvent) {
                    super.update(e)
                    val presentation = e.presentation
                    elements?.takeIf { it.size > 1 }?.let {
                        presentation.text = "${index + 1}/${it.size}"
                        presentation.isVisible = true
                    }
                        ?: run { presentation.isVisible = false }
                }
            }
        )
        val forward: ForwardAction = ForwardAction()
        forward.registerCustomShortcutSet(
            CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)),
            this
        )
        group.add(forward)
        group.add(createGearActionButton(openUsageView))
        val toolbar =
            ActionManager.getInstance().createActionToolbar(IMPLEMENTATION_VIEW_PLACE, group, true)
        toolbar.setReservePlaceAutoPopupIcon(false)
        toolbar.setTargetComponent(myEditor.contentComponent)
        return toolbar
    }

    private fun goBack() {
        index--
        updateControls()
    }

    private fun goForward() {
        index++
        updateControls()
    }

    fun showInUsageView(): UsageView? {
        return FindUtil.showInUsageView(null, collectElementsForShowUsages(), myTitle!!, project!!)
    }

    private inner class BackAction internal constructor() :
        AnAction(
            CodeInsightBundle.messagePointer("quick.definition.back"),
            AllIcons.Actions.Play_back
        ),
        ActionToIgnore {
        override fun actionPerformed(e: AnActionEvent) {
            goBack()
        }

        override fun update(e: AnActionEvent) {
            val presentation = e.presentation
            presentation.isEnabled = index > 0
            presentation.isVisible = elements != null && elements!!.size > 1
        }
    }

    private inner class ForwardAction internal constructor() :
        AnAction(
            CodeInsightBundle.messagePointer("quick.definition.forward"),
            AllIcons.Actions.Play_forward
        ),
        ActionToIgnore {
        override fun actionPerformed(e: AnActionEvent) {
            goForward()
        }

        override fun update(e: AnActionEvent) {
            val presentation = e.presentation
            presentation.isEnabled = elements != null && index < elements!!.size - 1
            presentation.isVisible = elements != null && elements!!.size > 1
        }
    }

    private inner class EditSourceAction internal constructor() :
        EditSourceActionBase(
            true,
            AllIcons.Actions.EditSource,
            CodeInsightBundle.message("quick.definition.edit.source")
        ) {
        override fun actionPerformed(e: AnActionEvent) {
            super.actionPerformed(e)
            if (myHint!!.isVisible) {
                myHint!!.cancel()
            }
        }
    }

    private open inner class EditSourceActionBase
    internal constructor(
        private val myFocusEditor: Boolean,
        icon: Icon?,
        text: @ActionText String?
    ) : AnAction(text, null, icon) {
        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled =
                fileChooserComboBox == null || !fileChooserComboBox!!.isPopupVisible
        }

        override fun actionPerformed(e: AnActionEvent) {
            elements!![index].navigate(myFocusEditor)
        }
    }

    private fun collectElementsForShowUsages(): Array<PsiElement> {
        val result: MutableList<PsiElement> = ArrayList()
        for (element in elements!!) {
            val psiElement = element.elementForShowUsages
            if (psiElement != null) {
                result.add(psiElement)
            }
        }
        return PsiUtilCore.toPsiElementArray(result)
    }

    companion object {
        private val TEXT_PAGE_KEY: @NonNls String? = "Text"
        private val BINARY_PAGE_KEY: @NonNls String? = "Binary"
        private const val IMPLEMENTATION_VIEW_PLACE = "ImplementationView"
        private fun createRenderer(project: Project?): ListCellRenderer<FileDescriptor?> {
            val mainRenderer: ListCellRenderer<FileDescriptor?> =
                object : ColoredListCellRenderer<FileDescriptor?>() {
                    override fun customizeCellRenderer(
                        list: JList<out FileDescriptor?>,
                        value: FileDescriptor?,
                        index: Int,
                        selected: Boolean,
                        hasFocus: Boolean
                    ) {
                        background = UIUtil.getListBackground(selected, true)
                        if (value != null) {
                            val element = value.myElement
                            setIcon(getIconForFile(value.myFile, project))
                            append(element.presentableText)
                            val presentation = element.containerPresentation
                            if (presentation != null) {
                                append("  ")
                                append(
                                    StringUtil.trimStart(
                                        StringUtil.trimEnd(presentation, ")"),
                                        "("
                                    ),
                                    SimpleTextAttributes.GRAYED_ATTRIBUTES
                                )
                            }
                        }
                    }
                }
            val rightRenderer: ListCellRenderer<FileDescriptor?> =
                object : SimpleListCellRenderer<FileDescriptor?>() {
                    override fun customize(
                        list: JList<out FileDescriptor?>,
                        value: FileDescriptor?,
                        index: Int,
                        selected: Boolean,
                        hasFocus: Boolean
                    ) {
                        foreground = UIUtil.getListForeground(selected, true)
                        if (value != null) {
                            text = value.myElement.locationText
                            icon = value.myElement.locationIcon
                        }
                    }
                }
            return LeftRightRenderer(mainRenderer, rightRenderer)
        }

        private fun update(
            elements: Collection<ImplementationViewElement>,
            f: PairFunction<in Array<ImplementationViewElement>, in List<FileDescriptor?>, Boolean>
        ) {
            val candidates: MutableList<ImplementationViewElement> = ArrayList(elements.size)
            val files: MutableList<FileDescriptor?> = ArrayList(elements.size)
            val names: MutableSet<String?> = HashSet()
            for (element in elements) {
                if (element.isNamed) {
                    names.add(element.name)
                }
                if (names.size > 1) {
                    break
                }
            }
            for (element in elements) {
                val file = element.containingFile ?: continue
                if (names.size > 1) {
                    files.add(FileDescriptor(file, element))
                } else {
                    files.add(FileDescriptor(file, element.containingMemberOrSelf))
                }
                candidates.add(element)
            }
            f.`fun`(candidates.toTypedArray(), files)
        }

        private fun getIconForFile(virtualFile: VirtualFile, project: Project?): Icon {
            return IconUtil.getIcon(virtualFile, 0, project)
        }

        fun getNewText(elt: PsiElement): String? {
            val project = elt.project
            val psiFile = getContainingFile(elt)
            val doc = PsiDocumentManager.getInstance(project).getDocument(psiFile!!) ?: return null
            if (elt.textRange == null) {
                return null
            }
            val implementationTextSelectioner =
                LanguageImplementationTextSelectioner.INSTANCE.forLanguage(elt.language)
            val start = implementationTextSelectioner.getTextStartOffset(elt)
            var end = implementationTextSelectioner.getTextEndOffset(elt)
            val rawDefinition = doc.charsSequence.subSequence(start, end)
            while (end > start &&
                StringUtil.isLineBreak(
                    rawDefinition[end - start - 1]
                )) { // removing trailing EOLs from definition
                end--
            }
            val lineStart = doc.getLineStartOffset(doc.getLineNumber(start))
            val lineEnd =
                if (end < doc.textLength) doc.getLineEndOffset(doc.getLineNumber(end))
                else doc.textLength
            val text = doc.charsSequence.subSequence(lineStart, lineEnd).toString()
            val processor = LanguageImplementationTextProcessor.INSTANCE.forLanguage(elt.language)
            return if (processor != null) processor.process(text, elt) else text
        }

        private fun getContainingFile(elt: PsiElement): PsiFile? {
            val psiFile = elt.containingFile ?: return null
            return psiFile.originalFile
        }
    }
}
