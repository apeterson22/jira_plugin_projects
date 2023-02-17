ScriptRunner_projectTemplateDynamic.groovy

This script gets the logged in user and their group memberships, and then gets the project template options based on the user's group membership. It then prompts the user to select a template, and creates the project based on the selected template. It also updates custom fields as needed.

Of course, this is just an example script, and you will need to provide the implementation for the getTemplate() and updateCustomFields() methods.

This script gets the logged in user and their group memberships, and then gets the project template options based on the user's group membership. It then prompts the user to select a template, and creates the project based on the selected template. It also updates custom fields as needed.

This implementation uses the getTemplate() method to filter the list of custom fields based on the user's group membership. It then returns a list of the names of the filtered fields.

The updateCustomFields() method takes a Project and a template (which is the name of a custom field in the template project). It retrieves the custom fields for the project and the template project, and maps the custom fields to their corresponding template fields based on their names.

It then updates the values of the custom fields for each issue in the project, using the values of the corresponding fields in the template project. It does this by creating an IssueInputParameters object and setting the custom field values to the values of the corresponding template fields. It then validates and updates each issue using the IssueService.

Note that this implementation assumes that the custom fields have the same names and types in both the project and the template project. If the fields have different names or types, you'll need to modify the code accordingly.