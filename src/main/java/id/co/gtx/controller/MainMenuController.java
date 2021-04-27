package id.co.gtx.controller;

import id.co.gtx.entity.Login;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope("desktop")
public class MainMenuController extends SelectorComposer<Component> {

    @Wire
    Tree treeMainMenu;
    @Wire
    Label lblInfo;
    @Wire
    Div xcontents;

    private Session session;

    EventListener<Event> onClickMenu = null;

    public MainMenuController() {
        onClickMenu = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Treeitem treeitem = (Treeitem) event.getTarget();
                openMenu(treeitem.getValue(), treeitem.getLabel());
            }
        };
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        session =  Executions.getCurrent().getSession();
        if (session.getAttribute("userLogin") == null) {
            Executions.sendRedirect("/login.zul");
        } else {
            assignEventTreeMenu(treeMainMenu.getChildren());
            Login login = (Login) session.getAttribute("userLogin");
            lblInfo.setValue("Username: " + login.getUsername());
        }
    }

    @Listen("onClick = #btnLogout")
    public void doLogout() {
        session.removeAttribute("userLogin");
        Executions.sendRedirect("/login.zul");
    }

    public void assignEventTreeMenu(List<Component> component) {
        component.stream().forEach(x->{
            assignEventTreeMenu(x.getChildren());
            if (x instanceof Treeitem)
                x.addEventListener(Events.ON_DOUBLE_CLICK, onClickMenu);
        });
    }

    public void openMenu(String menuLocation, String title){
        if (menuLocation != null) {
            xcontents.getChildren().clear();
            Executions.createComponents(menuLocation, xcontents, null);
        }
    }
}
