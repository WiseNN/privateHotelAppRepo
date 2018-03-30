package mainhotelapp

import devutil.ConsoleColors
import javafx.beans.value.ObservableValue
import javafx.scene.control.TextField

class UIUtil
{
    //validation
     fun validateText(observable: ObservableValue<out String>, oldValue:String, newValue:String, textField: TextField, isWhat : String )
    {

        when(isWhat)
        {
            "Name" -> {
                val regExpr = Regex("([a-z]|[A-Z])*")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }

            "City" -> {
                //allow for letters and 1 space
                val regExpr = Regex("([a-z]|[A-Z])*\\s?([a-z]|[A-Z])*")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }
            "State" -> {

                //Allow for 2 capital letters, between 0 and 2 occurrences
                val regExpr = Regex("([A-Z]){0,2}")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }
            "ZipCode" -> {
                //matches: 12345 or 12345-12345 (is forgiving, does not check if : 12345-12)
                val regExpr = Regex("^([0-9]{0,5})(-[0-9]{0,5})?\$")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "CC" -> {
                //matches: 4 digit number for credit card partial
                val regExpr = Regex("([0-9]{0,4})")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "SEC" ->{
                //matches: up to 12345, for credit card security code
                val regExpr = Regex("([0-9]{0,5})")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "Email" ->{
                //matches: up to A-Z,a-z,0-9,+,-,_,. then @(a word).[a-z](3) for email addresses
                val regExpr = Regex("^([a-zA-Z0-9]|'|\\.|\\+|\\-|_)*@?\\w*\\.?([a-z]{0,3})?\$")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "Phone" ->{
                //matches: up to A-Z,a-z,0-9,+,-,_,. then @(a word).[a-z](3) for email addresses
                var regExpr = Regex("\\d{0,10}")

                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

                //add dashes to the phone number

//                println("textField: "+textField.text.replace("(\\d{3})\\-?(\\d{3})\\-?(\\d{4})".toRegex(),"\$1-\$2-\$3"))

            }
            else -> System.out.println(ConsoleColors.yellowText("ERROR: The validation type: \"isWhat\" " +
                    "param: $isWhat is not accounted for in this when (switch) statement. \n " +
                    "See validateText() in Class: ReservationSummaryFragment)"))
        }

    }
}