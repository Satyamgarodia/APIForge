package com.satyam.apiforge.workspace;

import com.satyam.apiforge.security.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;

    // Create workspace
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateWorkspaceRequest request,
                                    @AuthenticationPrincipal User user) {
        System.out.println(request.name());
        System.out.println(request.slug());
        if (workspaceRepository.existsBySlug(request.slug())) {
            return ResponseEntity.badRequest().body("Slug already taken");
        }

        Workspace workspace = Workspace.builder()
                .name(request.name())
                .slug(request.slug())
                .owner(user)
                .build();

        return ResponseEntity.ok(workspaceRepository.save(workspace));
    }

    // Get all my workspaces
    @GetMapping
    public ResponseEntity<List<Workspace>> getMyWorkspaces(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                workspaceRepository.findByOwnerId(user.getId()));
    }

    // Get workspace by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id,
                                     @AuthenticationPrincipal User user) {
        return workspaceRepository.findById(id)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete workspace
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    @AuthenticationPrincipal User user) {
        return workspaceRepository.findById(id)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .map(w -> {
                    workspaceRepository.delete(w);
                    return ResponseEntity.ok().body("Deleted");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    record CreateWorkspaceRequest(String name, String slug) {}
}
