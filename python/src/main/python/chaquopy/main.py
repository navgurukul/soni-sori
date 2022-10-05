import traceback

# Returns empty string if successful execution, returns stack trace in case of error
def main(codeSnippet):
    try:
        exec(codeSnippet)
        return ""
    except:
        return traceback.format_exc()



