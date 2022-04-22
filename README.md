# spectra_xml

The purpose of this tool is to provide a CLI interface for Spectra Logic's tape libraries' XML interface. All commands are coded based on Spectra Logic's XML Command Reference Guide which is available at support.spectralogic.com.

Requires: sv_utils-1.5.jar (https://github.com/etruyj/sv_utils/releases/tag/v1.5) spectra_xml/lib/ directory to compile.

# Use

The executable slxml is located in the spectra_xml/bin/ directory. Linux and MacOS users can use the slxml shell script, while Windows users can use the slxml.bat command. A list of available flags can be found with the slxml -h or slxml --help command. For a list of available XML commands use slxml -c help. Non-secure (HTTP) connections must be specified with the --insecure or --http flag. Library IP address is specified with -e/--endpoint flag. User name is specified with -u/--username flag. Password is specified with -p/--password flag. Commands are specified with -c/--command flag.

Example: (List Partitions in Library)

./slxml -e IP_ADDRESS -u USER_NAME -p PASSWORD -c list-partitions --insecure

Advanced commands are available that string together the output of different XML requests in order to perform complex tasks, such as magazine-capacity and eject-empty-terapacks.

Do not change the library behavior or configuration without consulting Spectra Logic Technical Support.
