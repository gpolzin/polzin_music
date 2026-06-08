package com.polzin.music.ui.library;

import com.polzin.music.application.library.NavigationIndexService;
import com.polzin.music.infrastructure.db.repository.LinkRepository;
import com.polzin.music.infrastructure.filesystem.LinkService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibraryNavigationControllerTest {

    @Test
    void refreshesRelationshipFolders() {
        LinkRepository linkRepository = new LinkRepository();
        linkRepository.save("root-1", new LinkRepository.LinkRecord(
                new LinkService().createLinkState(Path.of("/links/a"), Path.of("/target/a"), false),
                "MUSICBRAINZ",
                "ref-1"
        ));

        LibraryNavigationController controller = new LibraryNavigationController(new NavigationIndexService(linkRepository));
        controller.refresh("root-1");

        assertEquals(1, controller.refreshCount());
    }
}
