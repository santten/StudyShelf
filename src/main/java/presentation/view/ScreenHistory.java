package presentation.view;

import presentation.controller.PageController;

import java.util.ArrayList;

public class ScreenHistory {
    static ArrayList<PageController> history;

    public ScreenHistory(){
        history = new ArrayList<>();
    }

    public void save(PageController controller){
        for (int i = 0; i < history.size(); i++){
            if (history.get(i).getPageName().equals(controller.getPageName())){
                jumpTo(i);
                return;
            }
        }
        history.add(controller);
    }

    public ArrayList<PageController> getHistory(){
        return history;
    }

    public void jumpTo(int i){
        PageController controller = history.get(i);

        if (controller != null) {
            history.subList(i + 1, history.size()).clear();
            controller.setPage();
        }
    }

    public void reset(){
        history.clear();
    }
}