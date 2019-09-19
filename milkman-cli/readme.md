# Milkman Commandline Interface

This is an experimental command line interface for plugin. After copying the content of the archive to the root directory of milkman,
you can start it by `mm` on terminal.

# Screenshot

![img](/img/gif/cli.gif)


# Features

* Editing Request-Aspects via `nano`
* Executing requests
* Analyzing responses via `less`


# Commands

* Note: all IDs are derived from the original names. All lowercase and special characters are replaced with `-`. E.g. `Your Workspace` becomes `your-workspace`*

| Command | Alias | Description | Arguments |
| ------ | --- |  -------  | ------ |
| change-workspace | ws | Switches currently activated workspace | workspace-id | 
| change-collection | col | Switches currently activated collection | collection   the name of the collection to switch to |
| execute-request | req | Executes a given request | requestish<sup>1</sup>     the name of the request to execute<br>-l, --less      outputs response into less<br>-v, --verbose   outputs all aspects |
| edit-request | e | Edits an aspect of a request | request   the name of the request to execute<br>aspect    the aspect to edit|
| quit | q | Quits Application | |

<sup>1</sup>A requestish is [[Workspace-Id/]Collection-Id/]Request-Id (i.e. the first two are optional)