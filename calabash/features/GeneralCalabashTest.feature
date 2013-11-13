@GeneralCalabash
Feature: general test of calabash scripts

	Scenario: I can delete a placeholder by touching it
		Given I wait up to 20 seconds to see "Add item"
		When I touch the "Placeholder 1" text
		Then I should not see "Placeholder 1"

	Scenario: I can add an item
		Given I wait up to 20 seconds to see "Add item"
		When I enter text "test" into field with id "etNewTask"
		Then I touch the "Add item" text 
		Then I should see "test"