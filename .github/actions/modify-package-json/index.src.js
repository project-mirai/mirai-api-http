const core = require('@actions/core');
const path = require('path');
const fs = require('fs');

try {
    const defaultChannel = core.getInput('defaultChannel');
    let channel = core.getInput('channel');
    const version = core.getInput('version');
    const srcPath = core.getInput('srcPath');
    const destPath = core.getInput('destPath');

    const workdir = process.env.GITHUB_WORKSPACE;
    const src = path.resolve(workdir, srcPath);
    const content = fs.readFileSync(src, 'utf8').toString();

    console.log(`read content: ${content}`);

    const packageJson = JSON.parse(content);

    if (defaultChannel) {
        packageJson.defaultChannel = defaultChannel;
    }

    if (!channel) {
        channel = packageJson.defaultChannel;
    }

    packageJson.channels[channel].push(version);

    const dest = path.resolve(workdir, destPath);
    const newContent = JSON.stringify(packageJson, null, 2)
    fs.writeFileSync(dest, newContent);

    console.log(`write content to ${dest}`);
    console.log(`new content: ${newContent}`)

    core.setOutput("outputPath", dest);

} catch (e) {
    core.setFailed(e.message);
}
