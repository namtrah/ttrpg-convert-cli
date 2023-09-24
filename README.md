# TTRPG convert CLI
![GitHub all releases](https://img.shields.io/github/downloads/ebullient/ttrpg-convert-cli/total?color=success)

A Command-Line Interface designed to convert TTRPG data from 5eTools (and upcoming support for Pf2eTools) into crosslinked, tagged, and formatted markdown optimized for [Obsidian.md](https://obsidian.md). 

<table><tr>
<td>Jump</td>
<td><a href="#install-the-command-line-utility">⬇ Download</a></td>
<td><a href="#conventions">📝 Conventions</a></td>
<td><a href="#recommended-plugins">🔌 Plugins</a></td>
</tr><tr>
<td><a href="#use-with-5etools-json-data">📖 5eTools</a></td>
<td><a href="#use-with-pf2etools-json-data">📖 Pf2eTools</a></td>
<td><a href="#templates">🎨 Templates</a></td>
<td><a href="#changes-that-impact-generated-templates-and-files">🚜 Changelog</a></td>
</tr></table>

I use [Obsidian](https://obsidian.md) to keep track of my campaign notes. This project parses json sources for materials that I own from the 5etools mirror to create linked and formatted markdown that I can reference in my notes.

> 🚜 Check out [Changes that impact generated templates and files](#changes-that-impact-generated-templates-and-files) and/or release notes for breaking changes and new capabilities. 

## Install the command line utility

There are several options for running `ttrpg-convert`. Choose whichever you are the most comfortable with: 

- [Use pre-built platform binary](#use-pre-built-platform-binary) (no Java required)
- [Use JBang](docs/usage/alternateRun.md#use-jbang)
- [Use Java to run the jar](docs/usage/alternateRun.md#use-java-to-run-the-jar)
- [Build from source](docs/usage/alternateRun.md#build-and-run-from-source)
- **Using Windows?** See the [Windows README](README-WINDOWS.md)

### Use pre-built platform binary

> 📝 Where do these binaries come from?  
> They are built on GitHub managed CI runners using the workflow defined [here](.github/workflows/release.yml), which compiles a Quarkus application (Java) into a platform-native binary using [GraalVM](https://www.graalvm.org/). I build and upload the mac arm64 binary myself (not supported by GH CI) using [this script](.github/workflows/augment-release.yml). 

[Download the latest release](https://github.com/ebullient/ttrpg-convert-cli/releases/latest) of the zip or tgz for your platform. Extract the archive. A `ttrpg-convert` binary executable will be in the extracted bin directory. 

In a terminal or command shell, navigate to the directory where you extracted the archive and run the command:

```shell
ttrpg-convert --help
```

We'll use this command in the instructions below.

Notes: 
- Folks familar with command line tools can add the `bin` directory to their path to make the command available from anywhere.

Notes for Windows:
- [Open a command prompt in a folder (Windows) ](https://www.lifewire.com/open-command-prompt-in-a-folder-5185505)
- [Running executables from the command line (Windows)](https://www.techwalla.com/articles/how-to-use-quotcdquot-command-in-command-prompt-window)
- To show emoji in Windows Commmand Prompt: `chcp 65001` and choose a font with emoji support (Consolas is one). You can also try the new Windows Terminal (`wt.exe`).
- See the [Windows README](README-WINDOWS.md)

Notes for MacOS:

- MacOS permission checking (unverified executable): `xattr -r -d com.apple.quarantine <path/to>/ttrpg-convert`

**Looking for a different method?** See [Alternate ways to run the CLI](docs/usage/alternateRun.md) for more options to download and run the CLI.

## Conventions

> 💭 **Recommendations** 
> - Keep generated content isolated in your vault and treat it as read-only.
> - Run the CLI against a separate directory. Use a comparison tool to preview changes.  
>   You can use `git diff` to compare arbitrary directories, for example:
>   ```
>   git diff --no-index vault/compendium/bestiary generated/compendium/bestiary
>   ```
>  - Use a copy tool that only updates modified files, like rsync or robocopy, to avoid unnecessary file copying when updating your vault.

- **Links.** Documents generated by this plugin will use markdown links rather than wiki links. A [css snippet](examples/css-snippets/hide-markdown-link-url.css) can make these links less invasive in edit mode by hiding the URL portion of the string.

- **File names.** To avoid conflicts and issues with different operating systems, all file names are slugified (all lower case, symbols stripped, and spaces replaced by dashes). This is a familiar convention for those used to jekyll, hugo, or other blogging systems.

  File names for resources outside of the core books (PHB, MM, and DMG) have the abbreviated source name appended to the end to avoid file collisions.

- **Organization.** Files are generated in two roots: `compendium` and `rules`. The location of these roots is configurable (see below). These directories will be populated depending on the sources you have enabled. 

  - `compendium` contains files for items, spells, monsters, etc. 
    The `compendium` directory is further organized into subdirectories for each type of content. For example, all items are in the `compendium/items` directory.  

  - `rules` contains files for conditions, weapon properties, variant rules, etc. 

- **Styles.** Every document has a `cssclasses` attribute that assigns a CSS class. We have some [CSS snippets](#optional-css-snippets) that you can use to customize elements of the compendium. 
  - 5e tools: `json5e-background`, `json5e-class`, `json5e-deck`, `json5e-deity`, `json5e-feat`, `json5e-hazard`, `json5e-item`, `json5e-monster`, `json5e-note`, `json5e-object`, `json5e-psionic`, `json5e-race`, `json5e-reward`, `json5e-spell`, and `json5e-vehicle`.
  - pf2e tools: `pf2e`, `pf2e-ability`, `pf2e-action`, `pf2e-affliction`, `pf2e-archetype`, `pf2e-background`, `pf2e-book`, `pf2e-delity`, `pf2e-feat`, `pf2e-hazard`, `pf2e-index`, `pf2e-item`, `pf2e-note`, `pf2e-ritual`, `pf2e-sleep`, `pf2e-trait`, 

- **Admonitions.** 
  - `ad-statblock`
  - 5e tools: `ad-flowchart`, `ad-gallery`, `ad-embed-action`, `ad-embed-feat`, `ad-embed-monster`, `ad-embed-object`, `ad-embed-race`, `ad-embed-spell`, `ad-embed-table`
  - pf2e tools: `ad-embed-ability`, `ad-embed-action`, `ad-embed-affliction`, `ad-embed-avatar`, `ad-embed-disease`, `ad-embed-feat`, `ad-embed-item`, `ad-pf2-note`, `ad-pf2-ritual`.

## Recommended plugins 

- **[Admonitions](obsidian://show-plugin?id=obsidian-admonition)**: The admonitions plugin supports a codeblock style that is used for more complicated content like statblocks, where callout syntax would be difficult to manage. 

    Import one or more admonition json files in the [examples](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples) directory to create the custom admonition types used for converted content:

    - [admonitions-5e.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/admonitions-5e.json) for 5e tools
    - [admonitions-pf2e.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/admonitions-pf2e.json) for pf2e tools
    - [other-admonitions.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/other-admonitions.json) if they are interesting

- **[Force note view mode by front matter](obsidian://show-plugin?id=obsidian-view-mode-by-frontmatter)**: I use this plugin to treat these generated notes as essentially read-only.  

    Ensure the plugin has the following options enabled: 

    - *"Ignore force view when not in front matter"*: the plugin will only change the view mode if `obsidianUIMode` is defined in the front matter.    
    - *"Ignore open files"*: the plugin won't try to change the view mode if the file is already open.

- **[TTRPG Statblocks](obsidian://show-plugin?id=obsidian-5e-statblocks)**: Templates for rendering monsters can define a `statblock` in the document body or provide a full or abridged yaml monster in the document header to update monsters in the plugin's bestiary. See [Templates](#templates) for more information.

- **[Initiative Tracker](obsidian://show-plugin?id=initiative-tracker)**: Templates for rendering monsters can include information in the header to define monsters that initiative tracker can use when constructing encounters.

### Optional CSS Snippets

Within the [examples/css-snippets](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples/css-snippets/) folder, you will find some CSS snippets that have been created to further customize the look of the generated content. They include:
- Functionality to float token images to side of a statblock.
- Further enhancement of the admonition styles.
- _PF2 Only_: More realistic looking Statblocks
- _PF2 Only_: Link Test to display action icons.
- _PF2 Only_: Light styling of pages as defined through css-classes.
- And much more.

#### Statblocks

Compendium (`*-compendium`) snippets include styles for statblocks.

If you aren't using a `*-compendium` snippet, you may want to download either `dnd5e-only-statblocks.css` or `pf2-only-statblocks.css` to style your statblocks.

> ⚠️ Do not use an `*-only-statblock.css` snippet and a `*-compendium.css` snippet together.

## Use with 5eTools JSON data

1. Create a shallow clone of the 5eTools mirror repo (which can/should be deleted afterwards):

    ```shell
    git clone --depth 1 https://github.com/5etools-mirror-1/5etools-mirror-1.github.io.git
    ```

2. Invoke the CLI. In this first example, let's generate indexes and markdown for SRD content:

    ```shell
    ttrpg-convert \
      --index \
      -o dm \
      5etools-mirror-1.github.io/data
    ```

    - `--index` Create `all-index.json` containing all of the touched artifact ids, and `src-index.json` that shows the filtered/allowed artifact ids. These files are useful when tweaking exclude rules (as shown below).
    - `-o dm` The target output directory. Files will be created in this directory.

    The rest of the command-line specifies input files:

    - `5etools-mirror-1.github.io/data` Path to the 5etools `data` directory (from a clone or release of the repo)

    This should produce a set of markdown files in the `dm` directory.

3. Invoke the command again, this time including sources and custom items:

    ```shell
    ttrpg-convert \
        --index \
        -o dm \
        -s PHB,DMG,SCAG \
        -c dm-sources.json \
        5etools-mirror-1.github.io/data \
        5etools-mirror-1.github.io/data/adventure/adventure-lox.json \
        5etools-mirror-1.github.io/data/book/book-aag.json \
        my-items.json 
    ```

    - `-s PHB,DMG,SCAG` Will include content from the Player's Handbook, the Dungeon Master's Guide, and the Sword Coast Adventurer's Guide (all sources I own).  
        > 🔸 **Source abbreviations** are found in the [source code (around line 138)](https://github.com/ebullient/ttrpg-convert-cli/blob/main/docs/sourceMap.md). Only use sources you own.  

    - `-c dm-sources.json` contains configuration parameters (shown in detail [below](#using-a-configuration-file))
    - Books (`/book/book-aag.json`) and adventures (`/adventure/adventure-lox.json`) to include as well.
    - `my-items.json` defines custom items for my campaign that follow 5etools JSON format.

## Use with Pf2eTools Data

🚜 🚧 🚜 🚧 🚜 🚧 🚜 🚧

1. Download a release of the Pf2eTools mirror, or create a shallow clone of the repo (which can/should be deleted afterwards):

    ```shell
    git clone --depth 1 https://github.com/Pf2eToolsOrg/Pf2eTools.git
    ```

2. Invoke the CLI. In this first example, let's generate indexes and use only SRD content (using the alias set up when [installing the cli](#install-the-command-line-utility)):

    ```shell
    ttrpg-convert \
      -g pf2e \
      --index \
      -o dm \
      Pf2eTools/data
    ```

    - `-g p2fe` The game system! Pathfinder 2e!
    - `--index` Create `all-index.json` containing all of the touched artifact ids, and `src-index.json` that shows the filtered/allowed artifact ids. These files are useful when tweaking exclude rules (as shown below).
    - `-o dm` The target output directory. Files will be created in this directory.

    The rest of the command-line specifies input files: 

    - `Pf2eTools/data` Path to the Pf2eTools `data` directory (from a clone or release of the repo)


## Using A Configuration File 

So far we've seen use of the tool using command line parameters. But this can get very clunky when trying to do any sort of complex command, plus it is a lot of typing! 

You can configure the CLI with a JSON or YAML file, either instead of, or along with, command line parameters. See [examples/config](examples/config) for the general config file structure in both formats. We'll use JSON as the preferred format, but you could also use YAML instead. It is your choice.

> 📝 JSON and YAML are both file formats for storing data in useful and human-readable ways.
> - If you want to know why the `{}` and `[]` are used in the ways that they are you can read about json *objects* and *arrays* [here](https://www.toolsqa.com/rest-assured/what-is-json/)).
> - YAML (Yet another markup language) has a [specification](https://yaml.org/spec/1.2/spec.html) that describes how it should be used.

Here is an example of a `config.json` file. As a JSON Object, it has a `"key": "value"` structure.

``` json
{
  "from": [
    "DMG",
    "PHB",
    "MM"
  ],
  "paths": {
    "compendium": "z_compendium/",
    "rules": "z_compendium/rules"
  }
}
```

This example performs two basic functions:

1. **Filter Input Sources:** Using the key `from`, we list the sources (that we own) that we want to include.
2. **Target file path:** Using the key `path`, we specify the target path for writing generated `compendium` and `rules` content. These directories will be created if they don't exist, and are relative to the output directory specified on the command line with `-o`.

**For additional information on configuration**, see [Configuration](docs/usage/configuration.md).

## Templates

This application uses the [Qute Templating Engine](https://quarkus.io/guides/qute). You can make simple customizations to markdown output by copying a template from `src/main/resources/templates`, making the desired modifications, and specifying that template in your configuration: 

```json
  "template": {
    "background": "examples/templates/tools5e/images-background2md.txt",
    "monster": "examples/templates/tools5e/monster2md-scores.txt"
  }
```

You would include this in the `config.json` as a base level key-value pair, and the entire file might look something like this: 

``` json
{
  "from": [
    "DMG",
    "PHB",
    "MM"
  ],
  "paths": {
    "compendium": "z_compendium/",
    "rules": "z_compendium/rules"
  },
  "template": {
    "background": "examples/templates/tools5e/images-background2md.txt",
    "monster": "examples/templates/tools5e/monster2md-scores.txt"
  }
}
```

The flag used to specify a template corresponds to the type of template being used. You can find the list of valid template keys in the [source code](https://github.com/ebullient/ttrpg-convert-cli/blob/main/src/main/resources/convertData.json) (look for `templateKeys`).

- Valid template keys for 5etools: `background`, `class`, `deck`, `deity`, `feat`, `hazard`, `item`, `monster`, `note`, `object`, `psionic`, `race`, `reward`, `spell`, `subclass`, `vehicle`.
- Valid template keys for Pf2eTools: `ability`, `action`, `affliction`, `archetype`, `background`, `book`, `deity`, `feat`, `hazard`, `inline-ability`, `inline-affliction`, `inline-attack`, `item`, `note`, `ritual`, `spell`, `trait`.

### Built-in / example templates

Not everything is customizable. In some cases, indenting, organizing, formatting, and linking text accurately is easier to do inline as a big blob. 

[Documentation](https://github.com/ebullient/ttrpg-convert-cli/tree/main/docs) is generated for template-accessible attributes.

- [Default templates](https://github.com/ebullient/ttrpg-convert-cli/tree/main/src/main/resources/templates)
- [Example templates](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples/templates)

Of particular note are the varied monster templates: 

- Admonition codeblock: [monster2md.txt](https://github.com/ebullient/ttrpg-convert-cli/tree/main/src/main/resources/templates/tools5e/monster2md.txt)
- Admonition codeblock with alternate score layout: [monster2md-scores.txt](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples/templates/tools5e/monster2md-scores.txt)
- TTRPG statblock in the body: [monster2md-yamlStatblock-body.txt](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples/templates/tools5e/monster2md-yamlStatblock-body.txt)
- Admonition codeblock in the body with minimal TTRPG/Initiative tracker YAML metadata in the header: [monster2md-yamlStatblock-header.txt](https://github.com/ebullient/ttrpg-convert-cli/tree/main/examples/templates/tools5e/monster2md-yamlStatblock-header.txt)

## Getting Help

- There is a `#cli-support` thread in the `#tabletop-games` channel of the [Obsidian Discord](https://discord.gg/veuWUTm).
- There is a `TTRPG-convert-help` post in the `obsidian-support` forum of the [Obsidian TTRPG Community Discord](https://discord.gg/Zpmr37Uv).

### Want to help fix it?

- If you're familiar with the command line and are comfortable running the tool, I hope you'll consider running [unreleased snapshots](docs/usage/alternateRun.md#run-unreleased-snapshots) and reporting issues.
- If you want to contribute, I'll take help of all kinds: documentation, examples, sample templates, stylesheets are just as important as Java code. See [CONTRIBUTING](CONTRIBUTING.md).

## Changes that impact generated templates and files

**Note:** Entries marked with "🔥" indicate crucial or breaking changes that might affect your current setup.

### 🔖 2.2.5: New templates for decks (and cards), legendary groups, and psionics

- **New templates**: `deck2md.txt`, `legendaryGroup2md.txt`, `psionic2md.txt`
  - Decks, when present, will be generated under `compendium/decks`. Cards are part of decks.
  - Legendary groups, when present, will be generated under `bestiary/legendary-groups`
  - Psionics, when present, will be generated under `compendium/psionics`.
- `feat2md.txt` is now also used for optional features.
- The default `monster2md.txt` template has been updated to embed the legendary group.
- CSS snippets have been updated to support legendary groups embedded in statblocks.

### 🔖 🔥 2.1.0: File name and path changes, template docs and attribute changes

1. 🔥 **Variant rules include the source in the file name**: this avoids duplicates (and there were some).
2. 🔥 **5eTools changed the classification for some creatures**, which moves them in the bestiary. Specifically: the Four-armed troll is a giant (and not an npc), a river serpent is a monstrosity rather than a beast, and ogre skeletons and red dracoliches are both undead.
3. 🔥 Better support for table rendering has superceded dedicated/hand-tended random name tables. All of the tables are still present, just in different units more directly related to source material.
4. 🔥 **Change to monster template attributes:** Legendary group attributes have been simplified to `name` and `desc`, just like other traits. See the [default monster template](https://github.com/ebullient/ttrpg-convert-cli/blob/0736c3929a6d90fe01860692f487b8523b57e60d/src/main/resources/templates/tools5e/monster2md.txt#L80) for an example.

> ***If you use the Templater plugin***, you can use [a templater script](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/migration/ttrpg-cli-renameFiles-5e-2.2.5.md) to **rename files in your vault before merging** with freshly generated content. View the contents of the template before running it, and adjust parameters at the top to match your Vault.

✨ **New template documentation** is available in [docs](https://github.com/ebullient/ttrpg-convert-cli/tree/main/docs). Content is generated from javadoc in the various *.qute packages (for template-accessible fields and methods). It may not be complete.. PRs to improve it are welcome.

### 🔖 🔥 2.0.0: File name and path changes, and styles!

1. 🔥 **A leading slash (`/`) is no longer used at the beginning of compendium and root paths**. This should allow you to move these two directories around more easily. 
    - I recommend that you keep the compendium and rules sections together as big balls of mud.
    - If you do want to further move files around, do so from within obsidian, so obsidian can update its links.

2. 🔥 **D&D 5e subclasses now use the source of the subclass in the file name**.

   > ***If you use the Templater plugin***, you can use [a templater script](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/migration/ttrpg-cli-renameFiles-1.3.0.md) to rename files in your vault before merging with freshly generated content. View the contents of the template before running it, and adjust parameters at the top to match your Vault.

3. 🎨 CSS styles for D&D 5e and Pathfinder are now available in `examples/css-snippets`. 

4. 📝 Admonitions are also available for import:
    - 🎨 [admonitions-5e.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/admonitions-5e.json)
    - [admonitions-pf2e-v3.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/admonitions-pf2e-v3.json)
    - 🎨 [other-admonitions.json](https://raw.githubusercontent.com/ebullient/ttrpg-convert-cli/main/examples/other-admonitions.json)

    Note: `admonitions-5e.json` and `other-admonitions.json` use colors from CSS snippets to adjust for light and dark mode.

**See [usage notes](docs/usage/README.md) for older changes.**

## Other notes

This project uses Quarkus, the Supersonic Subatomic Java Framework. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

This project is a derivative of [fc5-convert-cli](https://github.com/ebullient/fc5-convert-cli), which focused on working to and from FightClub5 Compendium XML files. It has also stolen some bits and pieces from [pockets-cli](https://github.com/ebullient/pockets-cli).
