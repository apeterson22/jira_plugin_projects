import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.ProjectCategory
import com.atlassian.jira.template.TemplateManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

def projectManager = ComponentAccessor.getProjectManager()
def userManager = ComponentAccessor.getUserManager()
def templateManager = ComponentAccessor.getTemplateManager()

// Get the project and template IDs
def projectKey = "PROJKEY"
def templateId = 1234 // Replace with the actual ID of the template

// Get the project and user
def project = projectManager.getProjectByCurrentKey(projectKey)
def user = userManager.getUserByName("admin")

// Create the new project
def newProject = new Project(
    project.getName() + " Copy",
    project.getKey() + "_copy",
    project.getLeadUser(),
    project.getProjectTypeKey(),
    new ProjectCategory(project.getProjectCategoryId())
)
newProject.setDescription(project.getDescription())

// Set the project permissions
def projectPermissions = projectManager.getProjectPermissions(newProject)
def userPermissions = projectPermissions.getUserPermissions(user)
userPermissions.addPermission("Create Issue")
projectManager.setProjectLead(newProject, user)
projectManager.updateProject(newProject)

// Copy the project template
templateManager.copyTemplate(templateId, newProject)

// Log the new project key
log.warn("New project key: " + newProject.getKey())
