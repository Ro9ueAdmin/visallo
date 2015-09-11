package org.visallo.web.routes.vertex;

import com.google.inject.Inject;
import com.v5analytics.webster.ParameterizedHandler;
import com.v5analytics.webster.annotations.Handle;
import com.v5analytics.webster.annotations.Required;
import org.vertexium.Authorizations;
import org.vertexium.Graph;
import org.vertexium.Vertex;
import org.visallo.core.model.workQueue.Priority;
import org.visallo.core.model.workspace.WorkspaceHelper;
import org.visallo.core.user.User;
import org.visallo.core.util.SandboxStatusUtil;
import org.visallo.web.VisalloResponse;
import org.visallo.web.clientapi.model.SandboxStatus;
import org.visallo.web.parameterProviders.ActiveWorkspaceId;

public class VertexRemove implements ParameterizedHandler {
    private final Graph graph;
    private final WorkspaceHelper workspaceHelper;

    @Inject
    public VertexRemove(
            final Graph graph,
            final WorkspaceHelper workspaceHelper
    ) {
        this.graph = graph;
        this.workspaceHelper = workspaceHelper;
    }

    @Handle
    public void handle(
            @Required(name = "graphVertexId") String graphVertexId,
            @ActiveWorkspaceId String workspaceId,
            User user,
            Authorizations authorizations,
            VisalloResponse response
    ) throws Exception {
        Vertex vertex = graph.getVertex(graphVertexId, authorizations);
        if (vertex == null) {
            response.respondWithNotFound("Could not find vertex: " + graphVertexId);
            return;
        }

        SandboxStatus sandboxStatus = SandboxStatusUtil.getSandboxStatus(vertex, workspaceId);

        boolean isPublicVertex = sandboxStatus == SandboxStatus.PUBLIC;

        workspaceHelper.deleteVertex(vertex, workspaceId, isPublicVertex, Priority.HIGH, authorizations, user);
        response.respondWithSuccessJson();
    }
}
