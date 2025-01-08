package com.clothassistv3;

public class HomeItem {
    private String title;
    private String description;
    private boolean expanded;

    public HomeItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.expanded = false;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
