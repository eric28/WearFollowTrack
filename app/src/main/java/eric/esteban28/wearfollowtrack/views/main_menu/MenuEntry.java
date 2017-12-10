package eric.esteban28.wearfollowtrack.views.main_menu;

public class MenuEntry {

    private String title;
    private MenuOption menuOption;

    public MenuEntry(String title, MenuOption menuOption) {
        this.title = title;
        this.menuOption = menuOption;
    }

    public String getTitle() {
        return title;
    }


    public MenuOption getMenuOption() {
        return menuOption;
    }
}
