import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.bc.project.ProjectCreationData
import com.atlassian.jira.project.Project

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager

def getTemplate(String groupName) {
    ProjectManager projectManager = ComponentAccessor.getProjectManager()
    Project templateProject = projectManager.getProjectObjByKey("TEMPLATE")
    def templateFields = templateProject.getCustomFieldObjects()

    // Filter the list of template fields based on the user's group membership
    def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    def userGroups = ComponentAccessor.getUserUtil().getGroupNamesForUser(user)
    def filteredFields = templateFields.findAll { field ->
        def allowedGroups = field.getConfigurationSchemes().find { scheme ->
            scheme.getName() == "Allowed Groups"
        }.getAssociatedValues("Groups")
        allowedGroups.any { group -> userGroups.contains(group) }
    }

    return filteredFields.collect { field -> field.getName() }
}

def updateCustomFields(Project project, String template) {
    def templateProject = ComponentAccessor.getProjectManager().getProjectObjByKey("TEMPLATE")
    def templateFields = templateProject.getCustomFieldObjects()

    def customFieldManager = ComponentAccessor.getCustomFieldManager()
    def customFields = customFieldManager.getCustomFieldObjects(project)

    // Map the custom fields to their corresponding template fields
    def fieldMap = [:]
    customFields.each { field ->
        def templateField = templateFields.find { f -> f.getName() == field.getName() }
        if (templateField) {
            fieldMap[field] = templateField
        }
    }

    // Update the values of the custom fields based on the template
    def issueManager = ComponentAccessor.getIssueManager()
    def issueService = ComponentAccessor.getIssueService()
    def issues = issueManager.getIssueObjects(project.getIssues())
    issues.each { issue ->
        def issueInputParameters = issueService.newIssueInputParameters()
        fieldMap.each { customField, templateField ->
            def value = templateField.getValue(issue)
            if (value) {
                issueInputParameters.addCustomFieldValue(customField.getIdAsLong(), value)
            }
        }
        issueService.validateUpdate(user, issue.getId(), issueInputParameters)
    }
}


// Get the GroupManager
def groupManager = ComponentAccessor.getComponent(GroupManager)

// Get the logged in user and their group memberships
def loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def groups = groupManager.getGroupNamesForUser(loggedInUser?.getName())

// Get the project template options based on the user's group membership
def templateOptions = []
if (groups.contains("group1")) {
    templateOptions = ["Template1", "Template2"]
} else if (groups.contains("group2")) {
    templateOptions = ["Template3", "Template4"]
} else {
    templateOptions = ["Template5", "Template6"]
}

// Prompt the user to select a template
def selectedTemplate = askForTemplate(templateOptions)

// Get the selected template
def template = getTemplate(selectedTemplate)

// Create the project
def projectService = ComponentAccessor.getComponent(ProjectService)
def projectManager = ComponentAccessor.getComponent(ProjectManager)

def projectData = new ProjectCreationData.Builder()
        .withName(template.name)
        .withKey(template.key)
        .withLead(template.lead)
        .withProjectTypeKey(template.projectTypeKey)
        .withProjectTemplateKey(template.projectTemplateKey)
        .build()

def project = projectService.createProject(loggedInUser, projectData)
if (project) {
    projectManager.setProjectLead(project, template.lead)
    project.setDescription(template.description)

    // Update custom fields
    updateCustomFields(project, template)

    log.info("Project created: " + project.key)
}

def askForTemplate(templateOptions) {
    def input = templateOptions[0]
    if (templateOptions.size() > 1) {
        input = getFieldScreenRenderer().showDialog("Select a template", "Please select a template:", templateOptions)
    }
    return input
}

def getTemplate(templateName) {
    // Implementation to get the selected template
}

def updateCustomFields(project, template) {
    // Implementation to update custom fields
}
