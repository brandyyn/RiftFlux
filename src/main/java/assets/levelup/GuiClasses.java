package assets.levelup;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public final class GuiClasses extends GuiScreen {
    private static final int MAX_COLUMNS = 5;
    private static final int BUTTON_WIDTH = 96;
    private static final int COLUMN_SPACING = 8;
    private static final int ROW_SPACING = 28;
    private static final int DONE = 1000;
    private static final int CANCEL = 1001;
    private static final int PREVIOUS_PAGE = 1002;
    private static final int NEXT_PAGE = 1003;
    private boolean closedWithButton;
    private byte selectedClass;
    private int page;
    private GuiButton doneButton;
    private final GuiScreen parentScreen;

    public GuiClasses() {
        this(null);
    }

    public GuiClasses(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        boolean canSelectClass = LevelUpHUD.canSelectClass();
        if (this.doneButton != null) {
            this.doneButton.enabled = canSelectClass && ClassBonus.isValidClass(this.selectedClass);
        }
        int rows = this.getVisibleRows();
        int textY = this.getTop(rows) + rows * ROW_SPACING;
        this.drawCenteredString(this.fontRendererObj, this.getClassTooltip(), this.width / 2, textY, 0xFFFFFF);
        this.drawCenteredString(
                this.fontRendererObj,
                StatCollector.translateToLocalFormatted("gui.class.title", ClassBonus.getClassName(this.selectedClass)),
                this.width / 2,
                textY + 14,
                0xFFFFFF
        );
        if (!canSelectClass) {
            String levelRequired = StatCollector.translateToLocalFormatted("gui.class.level_required", ModConfig.levelUpClassSelectionLevel);
            this.drawCenteredString(this.fontRendererObj, levelRequired, this.width / 2, textY + 26, LevelUpHUD.getPulseColor());
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getClassTooltip() {
        if (!ClassBonus.isValidClass(this.selectedClass)) {
            return "";
        }
        StringBuilder tooltip = new StringBuilder(ClassBonus.getClassName(this.selectedClass)).append(": ");
        boolean added = false;
        for (int skillIndex = 0; skillIndex < ClassBonus.skillNames.length; ++skillIndex) {
            int bonus = ClassBonus.getConfiguredBonus(this.selectedClass, skillIndex);
            if (bonus <= 0) {
                continue;
            }
            if (added) {
                tooltip.append(", ");
            }
            tooltip.append('+').append(bonus).append(' ');
            tooltip.append(skillIndex == ClassBonus.skillNames.length - 1
                    ? StatCollector.translateToLocal("gui.class.skill_points")
                    : StatCollector.translateToLocal("skill" + (skillIndex + 1) + ".name"));
            added = true;
        }
        if (!added) {
            tooltip.append(StatCollector.translateToLocal("gui.class.no_starting_skills"));
        }
        tooltip.append(" | ").append(ClassBonus.getLocalizedBonuses(this.selectedClass));
        return tooltip.toString();
    }

    public void initGui() {
        this.closedWithButton = false;
        this.page = Math.min(this.page, this.getLastPage());
        this.rebuildButtons();
    }

    private void rebuildButtons() {
        this.buttonList.clear();
        int columns = this.getColumns();
        int classesPerPage = this.getClassesPerPage();
        int startClass = this.page * classesPerPage + 1;
        int visible = Math.max(0, Math.min(classesPerPage, ClassBonus.getClassCount() - startClass));
        int rows = Math.max(1, (visible + columns - 1) / columns);
        int top = this.getTop(rows);
        for (int offset = 0; offset < visible; ++offset) {
            int classIndex = startClass + offset;
            int column = offset % columns;
            int row = offset / columns;
            int buttonsInRow = Math.min(columns, visible - row * columns);
            int rowWidth = buttonsInRow * BUTTON_WIDTH + (buttonsInRow - 1) * COLUMN_SPACING;
            int rowLeft = (this.width - rowWidth) / 2;
            this.buttonList.add(new GuiButton(
                    classIndex,
                    rowLeft + column * (BUTTON_WIDTH + COLUMN_SPACING),
                    top + row * ROW_SPACING,
                    BUTTON_WIDTH,
                    20,
                    ClassBonus.getClassName(classIndex)
            ));
        }

        int actionY = top + rows * ROW_SPACING + 40;
        this.doneButton = new GuiButton(DONE, this.width / 2 + 96, actionY, 96, 20, StatCollector.translateToLocal("gui.done"));
        this.doneButton.enabled = LevelUpHUD.canSelectClass() && ClassBonus.isValidClass(this.selectedClass);
        this.buttonList.add(this.doneButton);
        this.buttonList.add(new GuiButton(CANCEL, this.width / 2 - 192, actionY, 96, 20, StatCollector.translateToLocal("gui.cancel")));
        if (this.page > 0) {
            this.buttonList.add(new GuiButton(PREVIOUS_PAGE, this.width / 2 - 44, actionY, 40, 20, "<"));
        }
        if (this.page < this.getLastPage()) {
            this.buttonList.add(new GuiButton(NEXT_PAGE, this.width / 2 + 4, actionY, 40, 20, ">"));
        }
    }

    private int getVisibleRows() {
        int columns = this.getColumns();
        int classesPerPage = this.getClassesPerPage();
        int startClass = this.page * classesPerPage + 1;
        int visible = Math.max(0, Math.min(classesPerPage, ClassBonus.getClassCount() - startClass));
        return Math.max(1, (visible + columns - 1) / columns);
    }

    private int getTop(int rows) {
        int contentHeight = rows * ROW_SPACING + 60;
        return Math.max(4, (this.height - contentHeight) / 2);
    }

    private int getColumns() {
        int available = Math.max(BUTTON_WIDTH, this.width - 16);
        return Math.max(1, Math.min(MAX_COLUMNS, (available + COLUMN_SPACING) / (BUTTON_WIDTH + COLUMN_SPACING)));
    }

    private int getClassesPerPage() {
        int rowsThatFit = Math.max(1, (this.height - 60) / ROW_SPACING);
        int rows = Math.min(Math.max(1, ModConfig.levelUpClassSelectionRows), rowsThatFit);
        return this.getColumns() * rows;
    }

    private int getLastPage() {
        return Math.max(0, Math.max(0, ClassBonus.getClassCount() - 2) / this.getClassesPerPage());
    }

    public void onGuiClosed() {
        if (this.closedWithButton && ClassBonus.isValidClass(this.selectedClass)) {
            FMLProxyPacket packet = SkillPacketHandler.getPacket(Side.SERVER, 1, this.selectedClass, new int[0]);
            LevelUp.classChannel.sendToServer(packet);
        }
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == DONE) {
            if (!LevelUpHUD.canSelectClass() || !ClassBonus.isValidClass(this.selectedClass)) {
                return;
            }
            this.closedWithButton = true;
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else if (button.id == CANCEL) {
            this.closedWithButton = false;
            this.mc.displayGuiScreen(this.parentScreen);
            if (this.parentScreen == null) {
                this.mc.setIngameFocus();
            }
        } else if (button.id == PREVIOUS_PAGE) {
            --this.page;
            this.rebuildButtons();
        } else if (button.id == NEXT_PAGE) {
            ++this.page;
            this.rebuildButtons();
        } else if (button.id > 0 && button.id < ClassBonus.getClassCount()) {
            this.selectedClass = (byte)button.id;
        }
    }
}
