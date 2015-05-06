Feature:
  Scenario Outline: Sample Selenium Usage
    Given I have access to Selenium
    When I visit page: '<url>'
    Then I should find in title: '<titlePart>'
  Examples:
    |  url                                      | titlePart   |
    |  https://github.com/epam/Gepard           | epam/Gepard |
    |  http://epam.github.io/Gepard/index.html  | Gepard      |