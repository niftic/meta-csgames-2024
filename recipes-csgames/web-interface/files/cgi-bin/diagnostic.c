#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(void) {
    // Set appropriate Content-Type header for CGI script output
    printf("Content-Type: text/plain; charset=utf-8\r\n\r\n");

    // Get user input from environment variables
    char host[200], type[200];
    char *arg1, *arg2, *arguments = getenv("QUERY_STRING");

    // Validate input
    if (arguments == NULL || strlen(arguments) == 0) {
        printf("No argument provided");
        return 1;
    } else if (strlen(arguments) > 200) {
        printf("Arguments too long");
        return 1;
    }
    arg1 = __strtok_r(arguments, "&", &arg2);
    if (sscanf(arg1, "host=%100s", host) != 1) {
        printf("No host argument provided");
        return 1;
    }
    if (sscanf(arg2, "type=%100s", type) != 1) {
        printf("No type argument provided");
        return 1;
    }

    // Execute the appropriate diagnostic tool based on user input
    char command[256];
    if (strcmp(type, "ping") == 0) {
        // Use ping
        snprintf(command, sizeof(command), "ping -c 4 %s 2>&1", host);
    } else if (strcmp(type, "traceroute") == 0) {
        // Use traceroute
        snprintf(command, sizeof(command), "traceroute %s 2>&1", host);
    } else {
        // Invalid diagnostic tool
        printf("Invalid diagnostic tool");
        return 1;
    }

    FILE *fp;
    char path[1024];

    /* Open the command for reading. */
    fp = popen(command, "r");
    if (fp == NULL) {
        printf("Failed to run command");
        return 1;
    }

    /* Read the output a line at a time - output it. */
    while (fgets(path, sizeof(path), fp) != NULL) {
        printf("%s", path);
    }

    /* close */
    pclose(fp);
    
    return 0;
}
