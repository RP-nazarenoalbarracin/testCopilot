# RecargaPay API Guidelines · Copilot Review Instructions

These are the rules that GitHub Copilot must apply when reviewing **Pull Requests** in this repository, focusing on changes to **controllers** and endpoint definitions.

---

## General Principles
- Endpoints **MUST** follow **OpenAPI 3.x** and RecargaPay internal API guidelines.
- **English** must be used in URL paths and JSON properties.
- Resources must be **plural** and URLs must be in **kebab-case**.
- JSON properties must be in **camelCase**.
- Avoid verbs in URLs (use subresources instead of `/create`, etc.).
- `/me` may be used as an alias for the authenticated user.

## HTTP Methods
- **POST** to create resources or subresources (201 on success, 400/401/403 on error).
- **GET** to retrieve collections or single resources.
- **PUT** replaces the entire resource.
- **PATCH** partially updates a resource.
- **DELETE** removes a resource by ID (204, no query deletes).

## Allowed HTTP Codes
- 200, 201, 202, 204
- 400, 401, 402, 403, 404, 406, 409, 412, 422, 429
- 500, 502, 503, 504  
  🚫 Do not use 408.

## Versioning
- Use header `x-api-version: YYYYMMDD`.
- Avoid path-based versioning unless strictly necessary.

## Pagination
- Endpoints returning collections **SHOULD** support pagination (`page`, `size`).
- Response must include `content`, `first`, `last`, `number`.

## Query Params
- Format must be **camelCase**.
- Multivalued params allowed as `param=1,2,3` or `param=1&param=2`.

## Data Formats
- DateTime: `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`.
- Money: `{ "currency":"BRL", "amount":12.84 }`.
- Phone numbers: E.164 format.
- Enums: UPPER-KEBAB-CASE strings.

## Idempotency
- All operations must be idempotent.
- For POST, use **idempotency key** when applicable.

## Headers
- Custom headers must start with `X-RP-` (`X-RP-USERID`, etc.).

## Webhooks
- Prefix endpoints with `/webhooks`.
- `{provider}` and `{event}` segments must be in kebab-case.

## Tasks
- Prefix endpoints with `/tasks`.
- Only **POST** may be used to trigger them.
- Request body must be JSON camelCase.

## Public API Tag (optional, controller-level)
- The tag with prefix `APIDoc2.0/*` is **optional** and applies **at controller level** (e.g., class annotation).
- **STRICT checks only apply when the controller class declares a `APIDoc2.0/*` tag.**
- If a controller does **not** have the `APIDoc2.0/*` tag, only baseline/general checks should be suggested (non-blocking style/consistency notes).

---

### Instructions for Copilot
- Focus review on files `*Controller.java` or `*Controller.kt`.
- Determine if the **controller class** has a `APIDoc2.0/*` tag (e.g., class-level `@Tag(name="APIDoc2.0/...")`):
  - **If present** → apply **STRICT checks**: OpenAPI 3.x annotations completeness, allowed HTTP codes, pagination on collections, date/money/phone/enum formats, idempotency for POST, custom headers `X-RP-*`, versioning header, URL/resource naming.
  - **If absent** → apply **baseline checks only** (general consistency/naming suggestions). Do **not** require the public tag.
- Leave clear inline comments with example fixes and reference the relevant section (“Source: STRICT checks”, “Source: Baseline”).
- Do not approve or request changes; comments must be resolved before merging (organization policy).
