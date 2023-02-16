import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.template.ProjectTemplate
import com.atlassian.jira.permission.ProjectPermissions
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.bc.project.ProjectService.CreateProjectValidationResult
import com.atlassian.jira.bc.project.ProjectService.CreateProjectValidationResult.ErrorCollection
import com.atlassian.jira.bc.project.ProjectService.CreateProjectValidationResult.ValidationMessage
import com.atlassian.jira.bc.project.ProjectService.CreateProjectValidationResult.ValidationError
import com.atlassian.jira.bc.project.ProjectService.CreateProjectValidationResult.ValidationResult

// Get the Project Manager
ProjectManager projectManager = ComponentAccessor.getProjectManager()

// Get the Project Template Manager
def projectTemplateManager = ComponentAccessor.getProjectTemplateManager()

// Get the Project Service
ProjectService projectService = ComponentAccessor.getComponent(ProjectService.class)

// Get the user
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Define the project template key
def projectTemplateKey = "your-project-template-key"

// Get the project template
ProjectTemplate projectTemplate = projectTemplateManager.getProjectTemplateByKey(projectTemplateKey)

// Check if the user has permissions to the project template via group permissions
if (projectTemplateManager.canUserCreateProjectFromTemplate(user, projectTemplate)) {

  // Set the project name and key
  def projectName = "Your project name"
  def projectKey = "YOURPROJECTKEY"

  // Create a map of project attributes
  def projectAttributes = [
    ProjectService.KEY_PROJECT_LEAD: user.name,
    ProjectService.KEY_NAME: projectName,
    ProjectService.KEY_PROJECT_KEY: projectKey,
    ProjectService.KEY_PROJECT_TYPE_KEY: "software",
    ProjectService.KEY_PROJECT_TEMPLATE_KEY: projectTemplateKey
  ]

  // Create the project
  CreateProjectValidationResult validationResult = projectService.validateCreateProject(user, projectAttributes)

  // Check if the project validation was successful
  if (validationResult.isValid()) {
    ProjectService.CreateProjectResult createProjectResult = projectService.createProject(validationResult)
    if (createProjectResult.isValid()) {
      println("Project created successfully.")
    } else {
      // Handle error while creating project
      println("Project creation failed.")
    }
  } else {
    // Handle project validation error
    ErrorCollection errorCollection = validationResult.getErrorCollection()
    List<ValidationMessage> errorMessages = errorCollection.getErrorMessages()
    for (ValidationMessage errorMessage : errorMessages) {
      List<ValidationError> errors = errorCollection.getErrors(errorMessage)
      for (ValidationError error : errors) {
        println("Validation error: " + error.getError())
      }
    }
  }
} else {
  // Handle error when user doesn't have permissions to project template
  println("User doesn't have permissions to project template.")
}
