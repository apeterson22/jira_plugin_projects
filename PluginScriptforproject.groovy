public class ProjectCreatorPermissionsPlugin implements JiraResolvable {
    private List<Template> templates;
    private List<String> customFields;

    public ProjectCreatorPermissionsPlugin() {
        this.templates = getTemplates();
        this.customFields = getCustomFields();
    }

    public void createProject(String projectName, String projectDescription, String templateName, String creatorName) {
        if (userCanCreateProject(creatorName)) {
            if (userCanUseTemplate(templateName, creatorName)) {
                Project project = new Project(projectName, projectDescription);
                project.setTemplate(templateName);
                for (String field : customFields) {
                    project.setCustomField(field, getFieldValueFromTemplate(field, templateName));
                }
                project.copyFieldsFromOtherProject(getLastUsedProject(creatorName));
                project.save();
            } else {
                throw new PermissionDeniedException("You do not have permission to use the specified template.");
            }
        } else {
            throw new PermissionDeniedException("You do not have permission to create a project.");
        }
    }

    private List<Template> getTemplates() {
        // implementation to get all available templates from Jira
    }

    private List<String> getCustomFields() {
        // implementation to get all custom fields available in Jira
    }

    private boolean userCanCreateProject(String userName) {
        // implementation to check if the user has permission to create a project
    }

    private boolean userCanUseTemplate(String templateName, String userName) {
        // implementation to check if the user has permission to use the specified template
    }

    private String getFieldValueFromTemplate(String field, String templateName) {
        // implementation to get the value of a field from a template
    }

    private Project getLastUsedProject(String userName) {
        // implementation to get the last project created by the user
    }
}
