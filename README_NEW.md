# GitHub Team Dashboard

Whether it is in your laptop or in a big screen, _GitHub Team Dashboard_ will allow you to comfortably visualize useful
information related to your GitHub team.

## Running the application

In order to use this application you need to generate a GitHub personal access token in GitHub's settings menu, under
_Developer settings_. The token requires these permissions:

- All permissions under _repo_ including itself.
- read:org

Make sure you store this token somewhere, as you can only see it once.

The token needs to be set in the environment variable `NUXT_GITHUB_TOKEN`.

### npm

You can run the application using npm. First install the dependencies:

```bash
npm ci
```

Then, you can run the application:

```bash
npm run dev
```

Optionally, you can set the environment variable `NUXT_GITHUB_TOKEN` in a `.env` file in the root of the project:

```
NUXT_GITHUB_TOKEN=<your_token>
```
