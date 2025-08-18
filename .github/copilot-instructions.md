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

## Public Tags
- All public endpoints must have a **tag** with prefix `APIDoc2.0/*`.

---

### Instructions for Copilot
- Focus review on files `*Controller.java` or `*Controller.kt`.
- Validate that endpoint definitions comply with all rules above.
- Leave clear comments when rules are violated, including examples of correction.
- Reference the relevant section in each comment (“Source: HTTP Codes”, “Source: Public Tags”, etc.).
- Do not approve or request changes: only leave comments that must be resolved before merging.
