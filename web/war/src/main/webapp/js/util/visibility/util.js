define([
    'util/component/attacher',
    'configuration/plugins/registry',
    'components/visibility/VisibilityViewer',
    'components/visibility/VisibilityEditor'
], function(Attacher, registry, VisibilityViewer, VisibilityEditor) {
    'use strict';

    /**
     * Plugin to configure the user interface for displaying and editing visibility authorization strings.
     *
     * The visibility component requires two FlightJS components registered for viewing and editing: TODO
     *
     * @param {string} editorComponentPath The path to {@link org.visallo.visibility~Editor} component
     * @param {string} viewerComponentPath The path to {@link org.visallo.visibility~Viewer} component
     */
    registry.documentExtensionPoint('org.visallo.visibility',
        'Implement custom interface for visibility display and editing',
        function(e) {
            return _.isString(e.editorComponentPath) ||
                _.isString(e.viewerComponentPath)
        },
        'http://docs.visallo.org/extension-points/front-end/visibility'
    );

    const defaultVisibility = {
        editorComponentPath: 'components/visibility/default/VisibilityEditor',
        viewerComponentPath: 'components/visibility/default/VisibilityViewer'
    };
    const point = 'org.visallo.visibility';
    let visibilityExtensions = registry.extensionsForPoint(point);


    if (visibilityExtensions.length === 0) {
        registry.registerExtension(point, defaultVisibility);
        visibilityExtensions = [defaultVisibility];
    }

    if (visibilityExtensions.length > 1) {
        console.warn('Multiple visibility extensions loaded', visibilityExtensions);
    }

    return {
        attachComponent: function(type, node, params) {
            const Component = type === 'viewer' ? VisibilityViewer : VisibilityEditor;
            const attacher = Attacher().node(node).component(Component).params(params);

            attacher.teardown({ react: true, flight: true });
            attacher.attach();

            return attacher;
        }
    };
});
