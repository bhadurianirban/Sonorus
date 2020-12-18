/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.login;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.leviosa.core.client.MenuClient;
import org.hedwig.cms.dto.MenuDTO;
import org.hedwig.cms.dto.MenuNode;
import org.primefaces.model.menu.DefaultMenuItem;

import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;

import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author bhaduri
 */
@Named(value = "menuController")
@RequestScoped
public class MenuController {

    private MenuModel menuModel;
    /**
     * Creates a new instance of MenuController
     */
    @Inject
    LoginController loginController;

    public MenuController() {
    }

    @PostConstruct
    void init() {
        if (loginController.getUserAuthDTO().getUserId() == null) {
            
        } else {
            menuModel = new DefaultMenuModel();
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
            MenuClient menuListGet = new MenuClient();
            menuDTO = menuListGet.getMenuTree(menuDTO);
            MenuNode authorisedMenuRoot = menuDTO.getRootMenuNode();
            List<MenuNode> rootMenuForest = authorisedMenuRoot.getChildren();
            DefaultSubMenu rootMenu = DefaultSubMenu.builder().label("User Menu").build();
            //LeviosaClientService ms = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
            buildMultiMenu(rootMenuForest, rootMenu);
            List<MenuElement> userMenuList = rootMenu.getElements();
            for (MenuElement userMenu : userMenuList) {
                menuModel.getElements().add(userMenu);
            }
            String termBrowseUrl = "/cms/browse/RootTermList?faces-redirect=true";
            DefaultMenuItem browseTerms = DefaultMenuItem.builder().value("Browse").outcome(termBrowseUrl).build();
            menuModel.getElements().add(browseTerms);
            //menuModel.addElement(menuMaker.getUserMenu());

        }
    }

    private void buildMultiMenu(List<MenuNode> menuForest, DefaultSubMenu userMenu) {
        for (MenuNode menuNode : menuForest) {

            if (menuNode.getChildren() == null) {
                //this is a leaf node
                String termName = menuNode.getTermName();
                String termUrl = menuNode.getTermUrl();
                termUrl = "/cms" + termUrl + "?faces-redirect=true&termslug=" + menuNode.getTermSlug();
                DefaultMenuItem userMenuLeafNode = DefaultMenuItem.builder().value(termName).outcome(termUrl).build();
                userMenu.getElements().add(userMenuLeafNode);
            } else {
                //this is not a leaf node
                DefaultSubMenu userMenuNode = DefaultSubMenu.builder().label(menuNode.getName()).build();
                userMenu.getElements().add(userMenuNode);
                buildMultiMenu(menuNode.getChildren(), userMenuNode);

            }
        }
    }

    public MenuModel getMenuModel() {
        return menuModel;
    }

    public void setMenuModel(MenuModel menuModel) {
        this.menuModel = menuModel;
    }

}
