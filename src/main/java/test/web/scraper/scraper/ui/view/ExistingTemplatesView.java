package test.web.scraper.scraper.ui.view;

import test.web.scraper.scraper.domain.service.ScraperTemplateService;

import test.web.scraper.scraper.domain.entity.ScraperTemplateEntity;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("ExistingTemplatesView")
@AnonymousAllowed
@Route(value = "existing-templates-view")
@Menu
public class ExistingTemplatesView extends VerticalLayout {

    private ComboBox<ScraperTemplateEntity> scraperTemplateEntityComboBox = new ComboBox<>("Select an existing template");

    public ExistingTemplatesView(
ScraperTemplateService scraperTemplateServi
) {        setWidthFull();
        setHeightFull();
        scraperTemplateEntityComboBox.getStyle().setWidth("100%");

        add(scraperTemplateEntityComboBox);
Grid<ScraperTemplateEntity> grid = new Grid<>(ScraperTemplateEntity.class);
grid.setItems(scraperTemplateServi.findAll());
grid.setColumns("name", "cssSelector", "urlPrefix");
        add(grid);
    }
}
