---
title: Development Process
---

Our development workflow bases on the standard [GitHub workflow](https://guides.github.com/introduction/flow/) with the slight variation that developers are working on forks of the main repository.
The workflow consists of the following steps:

0. [Fork](https://guides.github.com/activities/forking/) the main Saros repository. If you already have a fork of the repository, you can skip this step.
1. [Create a remote branch](https://help.github.com/articles/creating-and-deleting-branches-within-your-repository/) with a meaningful name.
2. Push changes to the new branch.
3. Create a [pull request](https://guides.github.com/activities/forking/#making-a-pull-request) onto the main repository. General information about pull requests can be found [here](https://help.github.com/articles/about-pull-requests/).
    * *(Optional)* Request other developers to review the pull request by asking them directly through [mentions](https://help.github.com/en/articles/basic-writing-and-formatting-syntax#mentioning-people-and-teams).
4. Improve the pull request (or explain your changes) until the reviewers are satisfied (see our [review process](review.md) for more information) and the [CI](https://www.saros-project.org/contribute/processes/continuous-integration.html) jobs run successfully.
5. Request that the pull request is merged into the main repository if
    * The CI builds are successful.
    * The pull request was approved by at least some of the reviewers.
    * There are no open requests for changes by a reviewer.

## Pull Request Structure

In the Saros project, we use two different strategies to merge pull requests: [squash and merge](#squash-and-merge) and [rebase and merge](#rebase-and-merge).
Which strategy is used to merge a particular pull request depends on the size, content and structure of the commits contained in the pull request.

Independently of the merge strategy, please make sure that all commits that are supposed to end up on the main repository have an [appropriate commit message](../guidelines.html#commit-message).

### [Squash and Merge](https://help.github.com/articles/about-pull-request-merges/#squash-and-merge-your-pull-request-commits)

This is the preferred merging strategy for small and coherent changes.

If your change only touches a specific file, functionality, or API and is not "to large" (this is somewhat subjective and will be up to you and the reviewers; as a general rule of thumb, it should not change more than 200 lines of code).
This allows the changes to be bundled into one concise commit.

### [Rebase and Merge](https://help.github.com/en/articles/about-pull-request-merges#rebase-and-merge-your-pull-request-commits)

This is the preferred merging strategy for larger changes.

If the pull request contains many changes (touches many different files/areas of the code and changes a large number of lines of code), we would prefer to split it into multiple commits make the review process easier and improve the usability of the git history.
This is especially true if your pull requests contains changes made by automated tools (i.e. refactorings) as they can be large in size but don't need to be reviewed as thoroughly.
So please make sure to structure you pull request accordingly by splitting it into sensible commits.

Furthermore, to enable us to retain this structure when merging the pull request, please make sure to keep the commit history of the pull request clean (only include commits that are supposed to end up on the main repository) and not to include any merge commits (as they unnecessarily pollute the history) in your pull request so that it can be easily rebased and merged.
This will most likely require you to rebase and amend your branch locally when updating your changes and then force push it to your fork to update the pull request.
