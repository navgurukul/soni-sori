
import sys
from os.path import dirname, join
from com.chaquo.python import Python


def main(CodeAreaData):
    file_dir = str(Python.getPlatform().getApplication().getFilesDir())
    filename = join(dirname(file_dir), 'file.txt')

    try:
        # First, save a reference to the original standard output
        original_stdout = sys.stdout

        # Now open a new file (file.txt) with the intention to write data and change standard output to our file
        sys.stdout = open(filename, 'w', encoding='utf8', errors="ignore")

        # Now execute our code using exec() method
        exec(CodeAreaData)  # It will execute our code and save output in the file

        # Now close the file after writing data
        sys.stdout.close()

        # Reset the standard output to its original value
        sys.stdout = original_stdout

        # Open the file and read the output
        output = open(filename, 'r').read()

    except Exception as e:
        # To handle error
        # If any error occurs in the code like syntax error, then take that error message
        # and include it in the output variable to show on screen
        sys.stdout = original_stdout

        # Take exception error in output
        output = str(e)

    # Finally, return output as a string
    return output
