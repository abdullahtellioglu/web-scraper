package test.web.scraper.scraper.ui.view;

import test.web.scraper.scraper.domain.component.DocumentTemplateExtractor;
import test.web.scraper.scraper.domain.entity.ScraperTemplateEntity;
import test.web.scraper.scraper.domain.service.ScraperTemplateService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@PageTitle("New Template")
@AnonymousAllowed
@Route(value = "new-template")
@Menu
public class NewTemplateView extends VerticalLayout {
    private Grid<DocumentTemplateExtractor.CssSelectorAndHtml> grid;
    private GridListDataView<DocumentTemplateExtractor.CssSelectorAndHtml> gridListDataView;
    private List<DocumentTemplateExtractor.CssSelectorAndHtml> items = new ArrayList<>();
    private TextField cssSelectorTextField;
    private TextField urlPathToFollowTemplate;
    private TextField nameOfSelector;

    private final ScraperTemplateService scraperTemplateService;

    public NewTemplateView(ScraperTemplateService scraperTemplateService) {
        this.scraperTemplateService = scraperTemplateService;
        configureGrid();
        setWidthFull();
        setHeightFull();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setAlignItems(Alignment.BASELINE);
        searchLayout.setWidthFull();

        TextField urlInput = new TextField("Enter URL");
        urlInput.setValue("https://en.wikipedia.org/wiki/ChatGPT");
        urlInput.setWidth("100%");
        Button searchButton = new Button("Search", new Icon("vaadin", "search"));
        searchButton.addClickListener(e -> {
            String url = urlInput.getValue();
            if (url == null) {
                return;
            }
            try {
                gridListDataView = grid.setItems(DocumentTemplateExtractor.extract(url));
                urlPathToFollowTemplate.setValue(url);
            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        });

        searchLayout.add(urlInput, searchButton);
        add(searchLayout);
        HorizontalLayout horizontallayout = new HorizontalLayout();
        horizontallayout.setHeightFull();
        add(horizontallayout);


        VerticalLayout verticallayout = new VerticalLayout();
        verticallayout.setHeightFull();
        verticallayout.setPadding(false);
        verticallayout.add(grid);

        SplitLayout splitLayout = new SplitLayout(SplitLayout.Orientation.VERTICAL);
        splitLayout.setHeightFull();
        splitLayout.setWidthFull();
        splitLayout.addToPrimary(grid);
        splitLayout.addToSecondary(createAddTemplateForm());
        verticallayout.add(splitLayout);
        horizontallayout.add(verticallayout);
        horizontallayout.getStyle().setWidth("100%");
    }


    private Component createAddTemplateForm() {
        VerticalLayout container = new VerticalLayout();
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setHeightFull();


        cssSelectorTextField = new TextField("Css Selector");
        urlPathToFollowTemplate = new TextField("URL prefix to Follow Template");
        nameOfSelector = new TextField("Name of the selected path");
        formLayout.add(cssSelectorTextField);
        formLayout.add(urlPathToFollowTemplate);
        formLayout.add(nameOfSelector);
        container.add(formLayout);
        Button button = new Button("Save");
        button.addClickListener(e -> {
            ScraperTemplateEntity scraperTemplateEntity = new ScraperTemplateEntity();
            scraperTemplateEntity.setCssSelector(cssSelectorTextField.getValue());
            scraperTemplateEntity.setName(nameOfSelector.getValue());
            scraperTemplateEntity.setUrlPrefix(urlPathToFollowTemplate.getValue());
            scraperTemplateService.save(scraperTemplateEntity);
            Notification.show("Saved Successfully");
            cssSelectorTextField.clear();
            nameOfSelector.clear();
            grid.deselectAll();
        });
        container.add(button);
        return container;
    }


    private static Component createFilterHeader(String labelText,
                                                Consumer<String> filterChangeConsumer) {
        NativeLabel label = new NativeLabel(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private void configureGrid() {
        grid = new Grid<>();
        grid.addColumn(new ComponentRenderer<Component, DocumentTemplateExtractor.CssSelectorAndHtml>(selector -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(false);
            layout.setPadding(false);
            layout.add(new H4(selector.cssSelector()));
            layout.add(new Paragraph(selector.html()));
            return layout;
        })).setWidth("100%").setHeader("Select a CSS selector").setKey("selectACssSelector");

        gridListDataView = grid.setItems(items);
//        createGridFilter();

        grid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(cssSelectorAndHtml -> {
                cssSelectorTextField.setValue(cssSelectorAndHtml.cssSelector());
            });
        });
        grid.setWidth("100%");
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    private void createGridFilter() {
        CssSelectorAndHtmlFilter cssSelectorAndHtmlFilter = new CssSelectorAndHtmlFilter(gridListDataView);

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();
        Grid.Column<DocumentTemplateExtractor.CssSelectorAndHtml> selectACssSelector = grid.getColumnByKey("selectACssSelector");
        headerRow.getCell(selectACssSelector).setComponent(
                createFilterHeader("Filter", cssSelectorAndHtmlFilter::setText));
    }

    private static class CssSelectorAndHtmlFilter {
        private final GridListDataView<DocumentTemplateExtractor.CssSelectorAndHtml> dataView;
        private String text;

        public CssSelectorAndHtmlFilter(GridListDataView<DocumentTemplateExtractor.CssSelectorAndHtml> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean test(DocumentTemplateExtractor.CssSelectorAndHtml cssSelectorAndHtml) {
            return matches(cssSelectorAndHtml.cssSelector(), text) || matches(cssSelectorAndHtml.html(), text);
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}
