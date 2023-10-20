package ca.ulaval.glo4003.repul.api.catalog;

import ca.ulaval.glo4003.repul.api.catalog.assembler.CatalogResponseAssembler;
import ca.ulaval.glo4003.repul.application.catalog.CatalogService;

public class CatalogResource {
    private final CatalogService catalogService;
    private final CatalogResponseAssembler catalogResponseAssembler = new CatalogResponseAssembler();

    public CatalogResource(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

}
